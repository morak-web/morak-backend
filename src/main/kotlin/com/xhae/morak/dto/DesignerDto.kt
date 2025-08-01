package com.xhae.morak.dto

import java.time.LocalDateTime

data class DesignerApplyRequest(val designerId: Long)
data class ApplyStatusResponse(val status: String)

data class ResultUploadResponse(
    val success: Boolean,
    val uploadedAt: LocalDateTime,
    val fileUrl: String
)

data class AIFeedbackResponse(
    val phase: String,
    val content: String
)
