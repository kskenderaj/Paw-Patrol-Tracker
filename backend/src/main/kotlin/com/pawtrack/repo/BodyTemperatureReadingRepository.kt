package com.pawtrack.repo

import com.pawtrack.domain.BodyTemperatureReading
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant
import java.util.UUID

interface BodyTemperatureReadingRepository : JpaRepository<BodyTemperatureReading, UUID> {
    fun findByUserIdAndRecordedAtBetweenOrderByRecordedAtAsc(
        userId: String,
        from: Instant,
        to: Instant,
    ): List<BodyTemperatureReading>
}
