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

    @PostMapping("/{projectId}/feedback")
    fun addFeedback(@PathVariable projectId: Long, @RequestBody req: FeedbackCreateRequest) =
        ResponseEntity.noContent().apply { projectService.addFeedback(projectId, req) }.build<Void>()

    @GetMapping("/{projectId}/results")
    fun getResultFile(@PathVariable projectId: Long, @RequestParam phase: String) =
        ResponseEntity.ok(projectService.getResultFile(projectId, phase))
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
