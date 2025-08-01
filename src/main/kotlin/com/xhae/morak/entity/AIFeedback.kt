package com.xhae.morak.entity
import jakarta.persistence.*

@Entity
data class AIFeedback(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val projectId: Long,

    val phase: String, // "MID" 또는 "FINAL"

    @Lob
    val content: String
)
