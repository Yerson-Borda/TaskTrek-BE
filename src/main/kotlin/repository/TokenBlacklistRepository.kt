package com.repository

import com.entities.BlacklistedTokenEntity
import com.entities.BlacklistedTokens
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.*

interface TokenBlacklistRepository {
    suspend fun blacklistToken(token: String, userId: UUID, expiresAt: Instant)
    suspend fun isTokenBlacklisted(token: String): Boolean
    suspend fun cleanExpiredTokens()
}

class TokenBlacklistRepositoryImpl : TokenBlacklistRepository {
    override suspend fun blacklistToken(token: String, userId: UUID, expiresAt: Instant) {
        transaction {
            BlacklistedTokenEntity.new {
                this.token = token
                this.userId = userId
                this.expiresAt = expiresAt
            }
        }
    }

    override suspend fun isTokenBlacklisted(token: String): Boolean {
        return transaction {
            BlacklistedTokenEntity.find { BlacklistedTokens.token eq token }.any()
        }
    }

    override suspend fun cleanExpiredTokens() {
        transaction {
            BlacklistedTokenEntity.find {
                BlacklistedTokens.expiresAt less Instant.now()
            }.forEach { it.delete() }
        }
    }
}
