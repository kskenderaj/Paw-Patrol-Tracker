package com.pawtrack.websocket

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.pawtrack.api.dto.HeartRateWebSocketMessage
import com.pawtrack.service.HealthMetricsService
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class HeartRateWebSocketHandler(
    private val objectMapper: ObjectMapper,
    private val healthMetricsService: HealthMetricsService,
) : TextWebSocketHandler() {
    override fun afterConnectionEstablished(session: WebSocketSession) {
        session.sendMessage(
            TextMessage(
                objectMapper.writeValueAsString(
                    mapOf(
                        "type" to "connected",
                        "message" to "Send JSON: {\"type\":\"heart_rate\",\"userId\":\"...\",\"bpm\":72,\"recordedAt\":\"optional ISO-8601\"}",
                    ),
                ),
            ),
        )
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val payload = try {
            objectMapper.readValue<HeartRateWebSocketMessage>(message.payload)
        } catch (_: Exception) {
            session.sendMessage(
                TextMessage(objectMapper.writeValueAsString(mapOf("type" to "error", "message" to "Invalid JSON"))),
            )
            return
        }
        if (payload.type != "heart_rate") {
            session.sendMessage(
                TextMessage(
                    objectMapper.writeValueAsString(
                        mapOf("type" to "error", "message" to "Expected type heart_rate"),
                    ),
                ),
            )
            return
        }
        if (payload.userId.isBlank() || payload.bpm !in 30..250) {
            session.sendMessage(
                TextMessage(
                    objectMapper.writeValueAsString(
                        mapOf("type" to "error", "message" to "userId required, bpm 30-250"),
                    ),
                ),
            )
            return
        }
        val saved = healthMetricsService.recordHeartRate(
            com.pawtrack.api.dto.HeartRateSampleRequest(
                userId = payload.userId,
                bpm = payload.bpm,
                recordedAt = payload.recordedAt,
            ),
        )
        session.sendMessage(
            TextMessage(
                objectMapper.writeValueAsString(
                    mapOf(
                        "type" to "heart_rate_saved",
                        "id" to saved.id.toString(),
                        "userId" to saved.userId,
                        "bpm" to saved.bpm,
                        "recordedAt" to saved.recordedAt.toString(),
                    ),
                ),
            ),
        )
    }

    override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
        // no-op; connection will close
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        // no-op
    }
}
