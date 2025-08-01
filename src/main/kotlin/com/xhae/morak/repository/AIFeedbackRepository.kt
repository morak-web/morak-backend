package com.xhae.morak.repository
import com.xhae.morak.entity.AIFeedback
import org.springframework.data.jpa.repository.JpaRepository

interface AIFeedbackRepository : JpaRepository<AIFeedback, Long> {
    fun findByProjectIdAndPhase(projectId: Long, phase: String): AIFeedback?
}
