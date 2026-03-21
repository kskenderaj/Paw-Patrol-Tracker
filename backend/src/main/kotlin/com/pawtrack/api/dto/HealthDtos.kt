package com.pawtrack.api.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

data class HeartRateSampleRequest(
    @field:NotBlank val userId: String,
    @field:NotNull @field:Min(30) @field:Max(250) val bpm: Int,
    val recordedAt: Instant? = null,
)

data class HeartRateSampleResponse(
    val id: UUID,
    val userId: String,
    val bpm: Int,
    val recordedAt: Instant,
)

data class HeartRateWebSocketMessage(
    @JsonProperty("type") val type: String = "heart_rate",
    @field:NotBlank val userId: String,
    @field:NotNull @field:Min(30) @field:Max(250) val bpm: Int,
    val recordedAt: Instant? = null,
)

data class DailyStepsUpsertRequest(
    @field:NotBlank val userId: String,
    @field:NotNull val day: LocalDate,
    @field:NotNull @field:Min(0) val steps: Int,
)

data class DailyStepsResponse(
    val id: UUID,
    val userId: String,
    val day: LocalDate,
    val steps: Int,
    val updatedAt: Instant,
)

data class SleepSessionRequest(
    @field:NotBlank val userId: String,
    @field:NotNull val startTime: Instant,
    @field:NotNull val endTime: Instant,
    val source: String? = null,
)

data class SleepSessionResponse(
    val id: UUID,
    val userId: String,
    val startTime: Instant,
    val endTime: Instant,
    val source: String?,
)

data class BodyTemperatureRequest(
    @field:NotBlank val userId: String,
    @field:NotNull val celsius: BigDecimal,
    val recordedAt: Instant? = null,
)

data class BodyTemperatureResponse(
    val id: UUID,
    val userId: String,
    val celsius: BigDecimal,
    val recordedAt: Instant,
)
