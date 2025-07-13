package com.xhae.morak.repository

import com.xhae.morak.entity.Project
import com.xhae.morak.entity.Result
import org.springframework.data.jpa.repository.JpaRepository

interface ResultRepository : JpaRepository<Result, Long> {
    fun findByProjectAndPhase(project: Project, phase: String): Result?
}