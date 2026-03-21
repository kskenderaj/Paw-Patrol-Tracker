package com.pawtrack.api

import com.pawtrack.api.dto.DailyStepsResponse
import com.pawtrack.api.dto.DailyStepsUpsertRequest
import com.pawtrack.service.HealthMetricsService
import jakarta.validation.Valid
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1/steps")
class StepsController(
    private val healthMetricsService: HealthMetricsService,
) {
    @PostMapping
    fun upsert(@Valid @RequestBody body: DailyStepsUpsertRequest): DailyStepsResponse =
        healthMetricsService.upsertDailySteps(body)

    @GetMapping("/day")
    fun getDay(
        @RequestParam userId: String,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) day: LocalDate,
    ): DailyStepsResponse = healthMetricsService.getDailySteps(userId, day)

    @GetMapping("/recent")
    fun listRecent(
        @RequestParam userId: String,
        @RequestParam(defaultValue = "14") limit: Int,
    ): List<DailyStepsResponse> = healthMetricsService.listRecentSteps(userId, limit)
}
