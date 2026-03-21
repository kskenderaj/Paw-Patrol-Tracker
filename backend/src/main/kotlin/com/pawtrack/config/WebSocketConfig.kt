package com.pawtrack.config

import com.pawtrack.websocket.HeartRateWebSocketHandler
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
@EnableWebSocket
class WebSocketConfig(
    private val heartRateWebSocketHandler: HeartRateWebSocketHandler,
) : WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(heartRateWebSocketHandler, "/ws/heart-rate")
            .setAllowedOrigins("*")
    }
}
