package com.pawtrack.repo

import com.pawtrack.domain.HeartRateReading
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant
import java.util.UUID

interface HeartRateReadingRepository : JpaRepository<HeartRateReading, UUID> {
    fun findByUserIdAndRecordedAtBetweenOrderByRecordedAtAsc(
        userId: String,
        from: Instant,
        to: Instant,
    ): List<HeartRateReading>
}
