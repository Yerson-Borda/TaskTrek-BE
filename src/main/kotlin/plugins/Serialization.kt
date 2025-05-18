package com.plugins

import com.utils.LocalDateTimeSerializer
import com.utils.UUIDSerializer
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import java.time.LocalDateTime
import io.ktor.server.application.Application
import io.ktor.server.application.install
import java.util.UUID

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(
            Json {
                serializersModule = SerializersModule {
                    contextual(LocalDateTime::class, LocalDateTimeSerializer)
                    contextual(UUID::class, UUIDSerializer)
                }
                encodeDefaults = true
                ignoreUnknownKeys = true
            }
        )
    }
}
