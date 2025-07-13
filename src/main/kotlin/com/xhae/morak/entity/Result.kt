package com.xhae.morak.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
data class Result(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @ManyToOne(fetch = FetchType.LAZY)
    val project: Project,
    val phase: String, // "MID", "FINAL"
    val fileUrl: String,
    val uploadedAt: LocalDateTime,
    val description: String
)