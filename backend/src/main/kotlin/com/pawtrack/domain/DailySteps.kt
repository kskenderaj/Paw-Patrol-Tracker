package com.pawtrack.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(
    name = "daily_steps",
    uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "day"])],
)
class DailySteps(
    @Column(name = "user_id", nullable = false, length = 64)
    var userId: String,

    @Column(nullable = false)
    var day: LocalDate,

    @Column(nullable = false)
    var steps: Int,

    @Column(nullable = false)
    var updatedAt: Instant,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null
}
