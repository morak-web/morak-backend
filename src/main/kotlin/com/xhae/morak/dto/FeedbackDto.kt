package com.xhae.morak.dto

import com.xhae.morak.entity.Feedback
import java.time.LocalDateTime

data class FeedbackDto(
    val feedbackId: Long,
    val fromUserId: Long,
    val phase: String,
    val content: String,
    val createdAt: LocalDateTime
) {
    companion object {
        fun of(entity: Feedback) = FeedbackDto(
            feedbackId = entity.id,
            fromUserId = entity.toUserId,   // 만약 Feedback 엔티티에 fromUserId가 있다면 그 필드 사용
            phase = entity.phase,
            content = entity.content,
            createdAt = entity.createdAt
        )
    }
}
