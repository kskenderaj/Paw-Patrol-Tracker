package com.pawtrack.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "body_temperature_readings")
class BodyTemperatureReading(
    @Column(nullable = false, length = 64)
    var userId: String,

    @Column(nullable = false, precision = 4, scale = 1)
    var celsius: BigDecimal,

    @Column(nullable = false)
    var recordedAt: Instant,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null
}
