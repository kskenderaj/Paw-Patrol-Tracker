package com.pawtrack.repo

import com.pawtrack.domain.SleepSession
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant
import java.util.UUID

interface SleepSessionRepository : JpaRepository<SleepSession, UUID> {
    fun findByUserIdAndStartTimeBetweenOrderByStartTimeDesc(
        userId: String,
        from: Instant,
        to: Instant,
    ): List<SleepSession>
}
