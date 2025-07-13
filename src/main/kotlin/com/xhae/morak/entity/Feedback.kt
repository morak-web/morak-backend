package com.xhae.morak.entity

import jakarta.persistence.*
import java.time.LocalDateTime
@Entity
data class Feedback(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @ManyToOne(fetch = FetchType.LAZY)
    val project: Project,
    val toUserId: Long,
    val phase: String,
    val content: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
)