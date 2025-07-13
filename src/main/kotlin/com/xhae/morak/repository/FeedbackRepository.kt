package com.xhae.morak.repository

import com.xhae.morak.entity.Feedback
import org.springframework.data.jpa.repository.JpaRepository

interface FeedbackRepository : JpaRepository<Feedback, Long>
