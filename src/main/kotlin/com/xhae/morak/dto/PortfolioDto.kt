package com.xhae.morak.dto

// PortfolioDto.kt
import com.xhae.morak.entity.Portfolio
import java.time.LocalDateTime

data class PortfolioDto(
    val portfolioId: Long,
    val title: String,
    val description: String,
    val fileUrl: String,
    val tags: List<String>,
    val createdAt: LocalDateTime
) {
    companion object {
        fun of(entity: Portfolio) = PortfolioDto(
            portfolioId = entity.id,
            title = entity.title,
            description = entity.description,
            fileUrl = entity.fileUrl,
            tags = entity.tags,
            createdAt = entity.createdAt
        )
    }
}
