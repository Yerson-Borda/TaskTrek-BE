package com.repository

import com.entities.UserEntity
import com.entities.Users
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

interface UserRepository {
    suspend fun createUser(
        username: String,
        email: String,
        password: String,
    ) : Boolean
    suspend fun findByEmail(email: String): UserEntity?
    suspend fun findById(userId: UUID): UserEntity?
}

class UserRepositoryImpl : UserRepository {
    override suspend fun createUser(
        username: String,
        email: String,
        password: String
    ): Boolean {
        try {
            transaction {
                UserEntity.new {
                    this.username = username
                    this.email = email
                    this.password = password
                }
            }
            return true
        } catch (e: Exception) {
            return false
        }
    }

    override suspend fun findByEmail(email: String): UserEntity? {
        return transaction {
            UserEntity.find { Users.email eq email }.firstOrNull()
        }
    }

    override suspend fun findById(userId: UUID): UserEntity? {
        return transaction {
            UserEntity.findById(userId)
        }
    }
}