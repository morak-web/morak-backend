package com.xhae.morak.repository

import com.xhae.morak.entity.Portfolio
import org.springframework.data.jpa.repository.JpaRepository

interface PortfolioRepository : JpaRepository<Portfolio, Long> {
    fun findAllByDesignerId(designerId: Long): List<Portfolio>
    fun findByIdAndDesignerId(id: Long, designerId: Long): Portfolio?
}
