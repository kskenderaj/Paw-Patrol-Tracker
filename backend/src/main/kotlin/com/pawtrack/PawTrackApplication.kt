package com.pawtrack

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PawTrackApplication

fun main(args: Array<String>) {
    runApplication<PawTrackApplication>(*args)
}
