package com.xhae.morak.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
data class Payment(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @ManyToOne(fetch = FetchType.LAZY)
    val project: Project,
    val amount: Int,
    val status: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
)