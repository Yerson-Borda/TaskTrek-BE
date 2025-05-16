package com.utils

import com.repository.TokenBlacklistRepository
import io.ktor.server.application.Application
import kotlinx.coroutines.*

fun Application.scheduleTokenCleanup(blacklistRepo: TokenBlacklistRepository) {
    launch {
        while (true) {
            delay(24 * 60 * 60 * 1000L) // 24hrs
            blacklistRepo.cleanExpiredTokens()
        }
    }
}
