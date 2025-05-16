package com.entities

import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.UUID
import java.time.Instant

object BlacklistedTokens : UUIDTable("blacklisted_tokens") {
    val token = varchar("token", 512).uniqueIndex()
    val expiresAt = timestamp("expires_at")
    val userId = uuid("user_id").references(Users.id)
    val createdAt = timestamp("created_at").clientDefault { Instant.now() }
}

class BlacklistedTokenEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<BlacklistedTokenEntity>(BlacklistedTokens)
    var token by BlacklistedTokens.token
    var expiresAt by BlacklistedTokens.expiresAt
    var userId by BlacklistedTokens.userId
    var createdAt by BlacklistedTokens.createdAt
}