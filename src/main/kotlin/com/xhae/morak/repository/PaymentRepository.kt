package com.xhae.morak.repository

import com.xhae.morak.entity.Payment
import com.xhae.morak.entity.Project
import org.springframework.data.jpa.repository.JpaRepository

interface PaymentRepository : JpaRepository<Payment, Long> {
    fun findAllByProject(project: Project): List<Payment>
}