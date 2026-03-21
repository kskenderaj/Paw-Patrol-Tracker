package com.pawtrack.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "heart_rate_readings")
class HeartRateReading(
    @Column(nullable = false, length = 64)
    var userId: String,

    @Column(nullable = false)
    var bpm: Int,

    @Column(nullable = false)
    var recordedAt: Instant,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null
}
