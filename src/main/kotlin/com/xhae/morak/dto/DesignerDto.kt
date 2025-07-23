package com.xhae.morak.dto

import java.time.LocalDateTime

data class DesignerApplyRequest(val designerId: Long)
data class ApplyStatusResponse(val status: String)

data class ResultUploadResponse(
    val success: Boolean,
    val uploadedAt: LocalDateTime,
    val fileUrl: String
)

data class PortfolioDto(
    val portfolioId: Long,
    val title: String,
    val description: String,
    val fileUrl: String,
    val tags: List<String>,
    val createdAt: LocalDateTime
)

data class FeedbackDto(
    val feedbackId: Long,
    val fromUserId: Long,
    val phase: String,
    val content: String,
    val createdAt: LocalDateTime
)

data class AIFeedbackResponse(
    val phase: String,
    val content: String
)
