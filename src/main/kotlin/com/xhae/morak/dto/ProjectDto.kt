package com.xhae.morak.dto

import java.time.LocalDateTime

data class ProjectCreateRequest(
    val categoryId: Int,
    val userRequirements: String,
    val targetDevice: List<String>,
    val referenceUrls: List<String>,
    val expectedScreens: Int,
    val dueDate: LocalDateTime,
    val budgetEstimate: Int
)

data class ProjectCreateResponse(
    val projectId: Long,
    val status: String
)

data class ProjectListItemDto(
    val projectId: Long,
    val title: String?,
    val status: String,
    val createdAt: LocalDateTime
)

data class DesignerDto(
    val designerId: Long,
    val name: String,
    val profileImageUrl: String
)

data class ResultDto(
    val fileUrl: String,
    val uploadedAt: LocalDateTime,
    val description: String
)

data class ProjectDetailDto(
    val projectId: Long,
    val title: String?,
    val userRequirements: String,
    val categoryId: Int,
    val targetDevice: List<String>,
    val referenceUrls: List<String>,
    val expectedScreens: Int,
    val aiSummary: String?,
    val status: String,
    val createdAt: LocalDateTime,
    val dueDate: LocalDateTime,
    val budgetEstimate: Int,
    val designer: DesignerDto?,
    val midResult: ResultDto?,
    val finalResult: ResultDto?
)

// AI질문
data class AIQuestionDto(
    val questionId: Long,
    val parentQuestionId: Long?,
    val question: String,
    val answer: String?,
    val depth: Int,
    val createdAt: LocalDateTime
)

data class AIAnswerRequest(
    val questionId: Long,
    val answer: String
)

data class AIAnswerResponse(
    val nextQuestions: List<AIQuestionDto>
)

data class AIResetResponse(
    val success: Boolean
)

// 피드백
data class FeedbackCreateRequest(
    val toUserId: Long,
    val phase: String,
    val content: String
)

// 결과물
data class ResultFileResponse(
    val phase: String,
    val fileUrl: String,
    val description: String
)

// 결제
data class PaymentCreateRequest(
    val projectId: Long,
    val amount: Int
)

data class PaymentResponse(
    val paymentId: Long,
    val projectId: Long,
    val amount: Int,
    val status: String,
    val createdAt: LocalDateTime
)
