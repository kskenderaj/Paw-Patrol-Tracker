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
@Table(name = "sleep_sessions")
class SleepSession(
    @Column(nullable = false, length = 64)
    var userId: String,

    @Column(nullable = false)
    var startTime: Instant,

    @Column(nullable = false)
    var endTime: Instant,

    @Column
    var source: String? = null,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null
}
