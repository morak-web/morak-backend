package com.xhae.morak.service

import com.xhae.morak.dto.FeedbackDto
import com.xhae.morak.repository.FeedbackRepository
import org.springframework.stereotype.Service

@Service
class FeedbackService(
    private val feedbackRepository: FeedbackRepository
) {
    fun getFeedbacksByProjectAndPhase(projectId: Long, phase: String): List<FeedbackDto> =
        feedbackRepository.findAllByProjectIdAndPhase(projectId, phase.uppercase())
            .map { FeedbackDto.of(it) }
}
