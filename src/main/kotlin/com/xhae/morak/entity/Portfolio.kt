package com.xhae.morak.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
data class Portfolio(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    val designer: Designer,

    var title: String,
    var description: String,
    var fileUrl: String,

    @ElementCollection
    var tags: List<String> = emptyList(),

    val createdAt: LocalDateTime = LocalDateTime.now()
)
