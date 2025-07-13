package com.xhae.morak.entity

import jakarta.persistence.*
import java.time.LocalDateTime


@Entity
data class AIQuestion(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @ManyToOne(fetch = FetchType.LAZY)
    val project: Project,
    @ManyToOne(fetch = FetchType.LAZY)
    val parentQuestion: AIQuestion? = null,
    @Lob val question: String,
    @Lob var answer: String? = null,
    val depth: Int = 0,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
