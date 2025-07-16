package com.xhae.morak.service

import com.xhae.morak.ai.GeminiService
import com.xhae.morak.entity.*
import com.xhae.morak.dto.*
import com.xhae.morak.repository.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ProjectService(
    private val projectRepository: ProjectRepository,
    private val aiQuestionRepository: AIQuestionRepository,
    private val designerRepository: DesignerRepository,
    private val resultRepository: ResultRepository,
    private val feedbackRepository: FeedbackRepository,
    private val paymentRepository: PaymentRepository,
    private val geminiClient: GeminiService
) {
    @Transactional
    fun createProject(request: ProjectCreateRequest): ProjectCreateResponse {
        val project = projectRepository.save(
            Project(
                categoryId = request.categoryId,
                userRequirements = request.userRequirements,
                targetDevice = request.targetDevice,
                referenceUrls = request.referenceUrls,
                expectedScreens = request.expectedScreens,
                dueDate = request.dueDate,
                status = "DRAFT",
                budgetEstimate = request.budgetEstimate
            )
        )
        // Gemini로 꼼꼼한 추가질문 트리 자동 생성 (최초 질문은 answer=null)
        val questions = geminiClient.generateFollowupQuestions(request.userRequirements)
        questions.forEachIndexed { idx, q ->
            aiQuestionRepository.save(
                AIQuestion(
                    project = project,
                    parentQuestion = null,
                    question = q,
                    answer = null,
                    depth = 0
                )
            )
        }
        return ProjectCreateResponse(project.id, project.status)
    }

    @Transactional
    fun submitProject(projectId: Long): Map<String, String> {
        val p = projectRepository.findById(projectId).orElseThrow { NoSuchElementException("Project not found") }
        p.status = "MATCHING"

        return mapOf("status" to "MATCHING")
    }

    fun getProjectDetail(projectId: Long): ProjectDetailDto {
        val p = projectRepository.findById(projectId).orElseThrow { NoSuchElementException("Project not found") }
        val designer = p.designer?.let { DesignerDto(it.id, it.name, it.profileImageUrl) }
        val midResult = resultRepository.findByProjectAndPhase(p, "MID")?.let { ResultDto(it.fileUrl, it.uploadedAt, it.description) }
        val finalResult = resultRepository.findByProjectAndPhase(p, "FINAL")?.let { ResultDto(it.fileUrl, it.uploadedAt, it.description) }
        return ProjectDetailDto(
            projectId = p.id,
            title = p.title,
            userRequirements = p.userRequirements,
            categoryId = p.categoryId,
            targetDevice = p.targetDevice,
            referenceUrls = p.referenceUrls,
            expectedScreens = p.expectedScreens,
            aiSummary = p.aiSummary,
            status = p.status,
            createdAt = p.createdAt,
            dueDate = p.dueDate,
            budgetEstimate = p.budgetEstimate,
            designer = designer,
            midResult = midResult,
            finalResult = finalResult
        )
    }

    fun getMyProjects(status: String?): List<ProjectListItemDto> {
        val list = if (status != null) projectRepository.findAll().filter { it.status == status } else projectRepository.findAll()
        return list.map { ProjectListItemDto(it.id, it.title, it.status, it.createdAt) }
    }

    // AI 질문 트리 전체 조회
    fun getAIQuestionTree(projectId: Long): List<AIQuestionDto> {
        val p = projectRepository.findById(projectId).orElseThrow()
        return aiQuestionRepository.findAllByProjectOrderByCreatedAt(p).map {
            AIQuestionDto(it.id, it.parentQuestion?.id, it.question, it.answer, it.depth, it.createdAt)
        }
    }

    // AI 질문 답변 및 후속질문 생성 (예시: 답변 입력 시, Gemini로 후속 질문 생성)
    @Transactional
    fun answerAIQuestion(projectId: Long, req: AIAnswerRequest): AIAnswerResponse {
        val p = projectRepository.findById(projectId).orElseThrow()
        val q = aiQuestionRepository.findById(req.questionId).orElseThrow()
        q.answer = req.answer
        aiQuestionRepository.save(q)
        // 답변 시 후속질문 1개 생성 (변경된 GeminiService)
        val followups = geminiClient.generateFollowupQuestions("${q.question}\n[유저답변] ${req.answer}")
        val newQuestions = followups.take(1).map {
            aiQuestionRepository.save(
                AIQuestion(
                    project = p,
                    parentQuestion = q,
                    question = it,
                    answer = null,
                    depth = q.depth + 1
                )
            )
        }
        return AIAnswerResponse(newQuestions.map {
            AIQuestionDto(it.id, it.parentQuestion?.id, it.question, it.answer, it.depth, it.createdAt)
        })
    }

    // AI 질문 답변 수정
    @Transactional
    fun patchAIAnswer(projectId: Long, answerId: Long, newAnswer: String): Map<String, Any> {
        val q = aiQuestionRepository.findById(answerId).orElseThrow()
        q.answer = newAnswer
        aiQuestionRepository.save(q)
        // 하위 추가질문 무효화 (삭제)
        val children = aiQuestionRepository.findByParentQuestion(q)
        children.forEach { it.answer = null; aiQuestionRepository.save(it) }
        // 새로운 후속질문 (Gemini 활용, 1개만 생성)
        val followups = geminiClient.generateFollowupQuestions("${q.question}\n[수정된 답변] $newAnswer")
        val next = followups.take(1).map {
            aiQuestionRepository.save(
                AIQuestion(
                    project = q.project,
                    parentQuestion = q,
                    question = it,
                    answer = null,
                    depth = q.depth + 1
                )
            )
        }
        return mapOf(
            "nextQuestions" to next.map {
                AIQuestionDto(it.id, it.parentQuestion?.id, it.question, it.answer, it.depth, it.createdAt)
            },
            "invalidatedAnswerIds" to children.map { it.id }
        )
    }

    // AI 질문 트리 전체 리셋
    @Transactional
    fun resetAIQuestions(projectId: Long): AIResetResponse {
        val p = projectRepository.findById(projectId).orElseThrow()
        aiQuestionRepository.findAllByProjectOrderByCreatedAt(p).forEach { aiQuestionRepository.delete(it) }
        val questions = geminiClient.generateFollowupQuestions(p.userRequirements)
        questions.forEach { q ->
            aiQuestionRepository.save(AIQuestion(project = p, parentQuestion = null, question = q, answer = null, depth = 0))
        }
        return AIResetResponse(true)
    }

    // 피드백
    fun addFeedback(projectId: Long, req: FeedbackCreateRequest) {
        val p = projectRepository.findById(projectId).orElseThrow()
        feedbackRepository.save(Feedback(project = p, toUserId = req.toUserId, phase = req.phase, content = req.content))
    }

    // 결과물 조회
    fun getResultFile(projectId: Long, phase: String): ResultFileResponse {
        val p = projectRepository.findById(projectId).orElseThrow()
        val res = resultRepository.findByProjectAndPhase(p, phase) ?: throw NoSuchElementException()
        return ResultFileResponse(res.phase, res.fileUrl, res.description)
    }

    // 결제 생성
    fun createPayment(req: PaymentCreateRequest): PaymentResponse {
        val p = projectRepository.findById(req.projectId).orElseThrow()
        val payment = paymentRepository.save(Payment(project = p, amount = req.amount, status = "PAID"))
        return PaymentResponse(payment.id, p.id, payment.amount, payment.status, payment.createdAt)
    }

    // 결제 내역
    fun getMyPayments(): List<PaymentResponse> =
        paymentRepository.findAll().map {
            PaymentResponse(it.id, it.project.id, it.amount, it.status, it.createdAt)
        }
}
