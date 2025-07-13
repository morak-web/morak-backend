package com.xhae.morak.repository

import com.xhae.morak.entity.Project
import org.springframework.data.jpa.repository.JpaRepository

interface ProjectRepository : JpaRepository<Project, Long>
