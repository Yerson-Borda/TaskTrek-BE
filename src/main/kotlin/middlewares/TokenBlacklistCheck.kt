package com.middlewares

import com.repository.TokenBlacklistRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.call
import io.ktor.server.response.respond

fun Application.installTokenBlacklistCheck(blacklistRepo: TokenBlacklistRepository) {
    intercept(ApplicationCallPipeline.Plugins) {
        val authHeader = call.request.headers["Authorization"]?.removePrefix("Bearer ")?.trim()
        if (authHeader != null) {
            val isBlacklisted = blacklistRepo.isTokenBlacklisted(authHeader)
            if (isBlacklisted) {
                call.respond(HttpStatusCode.Unauthorized, "Token has been blacklisted")
                finish()
            }
        }
    }
}
