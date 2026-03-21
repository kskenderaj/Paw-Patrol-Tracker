package com.pawtrack.api

import com.pawtrack.api.dto.HeartRateSampleRequest
import com.pawtrack.api.dto.HeartRateSampleResponse
import com.pawtrack.service.HealthMetricsService
import jakarta.validation.Valid
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/api/v1/heart-rate")
class HeartRateController(
    private val healthMetricsService: HealthMetricsService,
) {
    @PostMapping
    fun ingest(@Valid @RequestBody body: HeartRateSampleRequest): HeartRateSampleResponse =
        healthMetricsService.recordHeartRate(body)

    @GetMapping
    fun list(
        @RequestParam userId: String,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) from: Instant,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) to: Instant,
    ): List<HeartRateSampleResponse> = healthMetricsService.listHeartRate(userId, from, to)
}
