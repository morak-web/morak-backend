package com.xhae.morak.entity

import jakarta.persistence.*
import java.time.LocalDateTime


@Entity
data class Designer(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String,
    val profileImageUrl: String
)
