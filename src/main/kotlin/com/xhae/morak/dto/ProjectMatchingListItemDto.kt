package com.xhae.morak.dto

import com.xhae.morak.entity.Project
import java.time.LocalDateTime

data class ProjectMatchingListItemDto(
    val projectId: Long,
    val title: String,
    val userRequirements: String,
    val categoryId: Int,
    val targetDevice: List<String>,
    val expectedScreens: Int,
    val budgetEstimate: Int,
    val createdAt: LocalDateTime
) {
    companion object {
        fun of(entity: Project) = ProjectMatchingListItemDto(
            projectId = entity.id,
            title = entity.title ?: "",
            userRequirements = entity.userRequirements,
            categoryId = entity.categoryId,
            targetDevice = entity.targetDevice,
            expectedScreens = entity.expectedScreens,
            budgetEstimate = entity.budgetEstimate,
            createdAt = entity.createdAt
        )
    }
}
