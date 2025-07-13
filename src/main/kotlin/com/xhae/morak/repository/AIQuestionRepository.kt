package com.xhae.morak.repository

import com.xhae.morak.entity.AIQuestion
import com.xhae.morak.entity.Project
import org.springframework.data.jpa.repository.JpaRepository


interface AIQuestionRepository : JpaRepository<AIQuestion, Long> {
    fun findAllByProjectOrderByCreatedAt(project: Project): List<AIQuestion>
    fun findByProjectAndAnswerIsNull(project: Project): List<AIQuestion>
    fun findByParentQuestion(parentQuestion: AIQuestion): List<AIQuestion>
}