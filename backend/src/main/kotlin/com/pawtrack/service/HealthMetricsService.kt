package com.pawtrack.service

import com.pawtrack.api.dto.BodyTemperatureRequest
import com.pawtrack.api.dto.BodyTemperatureResponse
import com.pawtrack.api.dto.DailyStepsResponse
import com.pawtrack.api.dto.DailyStepsUpsertRequest
import com.pawtrack.api.dto.HeartRateSampleRequest
import com.pawtrack.api.dto.HeartRateSampleResponse
import com.pawtrack.api.dto.SleepSessionRequest
import com.pawtrack.api.dto.SleepSessionResponse
import com.pawtrack.domain.BodyTemperatureReading
import com.pawtrack.domain.DailySteps
import com.pawtrack.domain.HeartRateReading
import com.pawtrack.domain.SleepSession
import com.pawtrack.repo.BodyTemperatureReadingRepository
import com.pawtrack.repo.DailyStepsRepository
import com.pawtrack.repo.HeartRateReadingRepository
import com.pawtrack.repo.SleepSessionRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import java.time.LocalDate

@Service
class HealthMetricsService(
    private val heartRateReadingRepository: HeartRateReadingRepository,
    private val dailyStepsRepository: DailyStepsRepository,
    private val sleepSessionRepository: SleepSessionRepository,
    private val bodyTemperatureReadingRepository: BodyTemperatureReadingRepository,
) {
    @Transactional
    fun recordHeartRate(request: HeartRateSampleRequest): HeartRateSampleResponse {
        val at = request.recordedAt ?: Instant.now()
        val saved = heartRateReadingRepository.save(
            HeartRateReading(
                userId = request.userId,
                bpm = request.bpm,
                recordedAt = at,
            ),
        )
        return HeartRateSampleResponse(
            id = saved.id!!,
            userId = saved.userId,
            bpm = saved.bpm,
            recordedAt = saved.recordedAt,
        )
    }

    @Transactional(readOnly = true)
    fun listHeartRate(userId: String, from: Instant, to: Instant): List<HeartRateSampleResponse> =
        heartRateReadingRepository.findByUserIdAndRecordedAtBetweenOrderByRecordedAtAsc(userId, from, to)
            .map {
                HeartRateSampleResponse(
                    id = it.id!!,
                    userId = it.userId,
                    bpm = it.bpm,
                    recordedAt = it.recordedAt,
                )
            }

    @Transactional
    fun upsertDailySteps(request: DailyStepsUpsertRequest): DailyStepsResponse {
        val now = Instant.now()
        val existing = dailyStepsRepository.findByUserIdAndDay(request.userId, request.day)
        val entity = if (existing.isPresent) {
            val e = existing.get()
            e.steps = request.steps
            e.updatedAt = now
            dailyStepsRepository.save(e)
        } else {
            dailyStepsRepository.save(
                DailySteps(
                    userId = request.userId,
                    day = request.day,
                    steps = request.steps,
                    updatedAt = now,
                ),
            )
        }
        return DailyStepsResponse(
            id = entity.id!!,
            userId = entity.userId,
            day = entity.day,
            steps = entity.steps,
            updatedAt = entity.updatedAt,
        )
    }

    @Transactional(readOnly = true)
    fun getDailySteps(userId: String, day: LocalDate): DailyStepsResponse {
        val e = dailyStepsRepository.findByUserIdAndDay(userId, day)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "No steps for that day") }
        return DailyStepsResponse(
            id = e.id!!,
            userId = e.userId,
            day = e.day,
            steps = e.steps,
            updatedAt = e.updatedAt,
        )
    }

    @Transactional(readOnly = true)
    fun listRecentSteps(userId: String, limit: Int): List<DailyStepsResponse> =
        dailyStepsRepository.findByUserIdOrderByDayDesc(userId).take(limit.coerceIn(1, 90)).map {
            DailyStepsResponse(
                id = it.id!!,
                userId = it.userId,
                day = it.day,
                steps = it.steps,
                updatedAt = it.updatedAt,
            )
        }

    @Transactional
    fun createSleepSession(request: SleepSessionRequest): SleepSessionResponse {
        if (!request.endTime.isAfter(request.startTime)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "endTime must be after startTime")
        }
        val saved = sleepSessionRepository.save(
            SleepSession(
                userId = request.userId,
                startTime = request.startTime,
                endTime = request.endTime,
                source = request.source,
            ),
        )
        return SleepSessionResponse(
            id = saved.id!!,
            userId = saved.userId,
            startTime = saved.startTime,
            endTime = saved.endTime,
            source = saved.source,
        )
    }

    @Transactional(readOnly = true)
    fun listSleepSessions(userId: String, from: Instant, to: Instant): List<SleepSessionResponse> =
        sleepSessionRepository.findByUserIdAndStartTimeBetweenOrderByStartTimeDesc(userId, from, to).map {
            SleepSessionResponse(
                id = it.id!!,
                userId = it.userId,
                startTime = it.startTime,
                endTime = it.endTime,
                source = it.source,
            )
        }

    @Transactional
    fun recordBodyTemperature(request: BodyTemperatureRequest): BodyTemperatureResponse {
        val at = request.recordedAt ?: Instant.now()
        val saved = bodyTemperatureReadingRepository.save(
            BodyTemperatureReading(
                userId = request.userId,
                celsius = request.celsius,
                recordedAt = at,
            ),
        )
        return BodyTemperatureResponse(
            id = saved.id!!,
            userId = saved.userId,
            celsius = saved.celsius,
            recordedAt = saved.recordedAt,
        )
    }

    @Transactional(readOnly = true)
    fun listBodyTemperature(userId: String, from: Instant, to: Instant): List<BodyTemperatureResponse> =
        bodyTemperatureReadingRepository.findByUserIdAndRecordedAtBetweenOrderByRecordedAtAsc(userId, from, to).map {
            BodyTemperatureResponse(
                id = it.id!!,
                userId = it.userId,
                celsius = it.celsius,
                recordedAt = it.recordedAt,
            )
        }
}
