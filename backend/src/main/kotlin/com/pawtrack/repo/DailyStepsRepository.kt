package com.pawtrack.repo

import com.pawtrack.domain.DailySteps
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate
import java.util.Optional
import java.util.UUID

interface DailyStepsRepository : JpaRepository<DailySteps, UUID> {
    fun findByUserIdAndDay(userId: String, day: LocalDate): Optional<DailySteps>

    fun findByUserIdOrderByDayDesc(userId: String): List<DailySteps>
}
