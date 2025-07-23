package com.xhae.morak.controller

import com.xhae.morak.dto.*
import com.xhae.morak.service.ProjectService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/projects")
class ProjectController(
    private val projectService: ProjectService
) {
    @PostMapping
    fun createProject(@RequestBody req: ProjectCreateRequest) =
        ResponseEntity.ok(projectService.createProject(req))

    @GetMapping("/{projectId}")
    fun getProjectDetail(@PathVariable projectId: Long) =
        ResponseEntity.ok(projectService.getProjectDetail(projectId))

    @GetMapping("/my")
    fun getMyProjects(@RequestParam(required = false) status: String?) =
        ResponseEntity.ok(projectService.getMyProjects(status))

    @GetMapping("/{projectId}/ai/questions/tree")
    fun getAIQuestionTree(@PathVariable projectId: Long) =
        ResponseEntity.ok(projectService.getAIQuestionTree(projectId))

    @PostMapping("/{projectId}/ai/answers")
    fun answerAIQuestion(@PathVariable projectId: Long, @RequestBody req: AIAnswerRequest) =
        ResponseEntity.ok(projectService.answerAIQuestion(projectId, req))

    @PatchMapping("/{projectId}/ai/answers/{answerId}")
    fun patchAIAnswer(@PathVariable projectId: Long, @PathVariable answerId: Long, @RequestBody req: AIAnswerRequest) =
        ResponseEntity.ok(projectService.patchAIAnswer(projectId, answerId, req.answer))

    @PostMapping("/{projectId}/ai/questions/reset")
    fun resetAIQuestions(@PathVariable projectId: Long) =
        ResponseEntity.ok(projectService.resetAIQuestions(projectId))

    @PatchMapping("/{projectId}/submit")
    fun submitProject(@PathVariable projectId: Long) =
        ResponseEntity.ok(
            projectService.submitProject(projectId)
        )

    @PostMapping("/{projectId}/feedback")
    fun addFeedback(@PathVariable projectId: Long, @RequestBody req: FeedbackCreateRequest) =
        ResponseEntity.noContent().apply { projectService.addFeedback(projectId, req) }.build<Void>()

    @GetMapping("/{projectId}/results")
    fun getResultFile(@PathVariable projectId: Long, @RequestParam phase: String) =
        ResponseEntity.ok(projectService.getResultFile(projectId, phase))


    // 1. 매칭 대기(지원 가능) 프로젝트 목록
    @GetMapping("/matching")
    fun getMatchingProjects(): ResponseEntity<List<ProjectMatchingListItemDto>> =
        ResponseEntity.ok(projectService.getMatchingProjects())

    // 3. 프로젝트 지원(디자이너 매칭)
    @PostMapping("/{projectId}/apply")
    fun applyProject(
        @PathVariable projectId: Long,
        @RequestBody req: DesignerApplyRequest // { designerId: Long }
    ): ResponseEntity<ApplyStatusResponse> =
        ResponseEntity.ok(projectService.applyToProject(projectId, req.designerId))

    // 4. 내 작업 목록(진행/완료)
    @GetMapping("/my/design")
    fun getMyDesignProjects(
        @RequestParam status: String?,
        @RequestParam designerId: Long // 인증/세션 대신 쿼리로 예시 (실제 서비스에선 세션/토큰 기반)
    ): ResponseEntity<List<ProjectListItemDto>> =
        ResponseEntity.ok(projectService.getMyDesignProjects(designerId, status))

    // 6. 중간/최종 결과물 제출 (이미지/PDF만, multipart/form-data)
    @PostMapping("/{projectId}/results")
    fun uploadProjectResult(
        @PathVariable projectId: Long,
        @RequestParam phase: String,
        @RequestParam description: String,
        @RequestPart file: MultipartFile
    ): ResponseEntity<ResultUploadResponse> =
        ResponseEntity.ok(projectService.uploadResult(projectId, phase, description, file))

    // 7. 의뢰인 피드백 확인
    @GetMapping("/{projectId}/feedback")
    fun getProjectFeedback(
        @PathVariable projectId: Long,
        @RequestParam phase: String
    ): ResponseEntity<List<FeedbackDto>> =
        ResponseEntity.ok(feedbackService.getFeedbacksByProjectAndPhase(projectId, phase))

    // 1. AI 피드백 (MID/FINAL)
    @GetMapping("/{projectId}/ai-feedback")
    fun getAiFeedback(
        @PathVariable projectId: Long,
        @RequestParam phase: String
    ): ResponseEntity<AIFeedbackResponse> =
        ResponseEntity.ok(aiService.getAIFeedback(projectId, phase))
}

@RestController
@RequestMapping("/api/payments")
class PaymentController(
    private val projectService: ProjectService
) {
    @PostMapping
    fun createPayment(@RequestBody req: PaymentCreateRequest) =
        ResponseEntity.ok(projectService.createPayment(req))

    @GetMapping("/my")
    fun getMyPayments() =
        ResponseEntity.ok(projectService.getMyPayments())
}
