package com.xhae.morak.entity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
data class Project(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val title: String? = null,
    val categoryId: Int,
    @Lob val userRequirements: String,
    @ElementCollection val targetDevice: List<String>,
    @ElementCollection val referenceUrls: List<String>,
    val expectedScreens: Int,
    val dueDate: LocalDateTime,
    val budgetEstimate: Int,
    var status: String = "DRAFT",
    val aiSummary: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @ManyToOne(fetch = FetchType.LAZY)
    var designer: Designer? = null
)