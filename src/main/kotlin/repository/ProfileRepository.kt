package com.repository

import com.entities.UserEntity
import com.entities.Users
import com.exceptions.AppException
import com.model.response.profile.Profile
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

interface ProfileRepository {
    suspend fun getProfile(email: String): Profile
    suspend fun updateUser(userId: UUID, username: String?, email: String?): UserEntity
}

class ProfileRepositoryImpl : ProfileRepository {
    override suspend fun getProfile(email: String): Profile {
        val user = transaction {
            UserEntity.find { Users.email eq email }.firstOrNull()
        } ?: throw AppException.NotFoundException("User not found", email)

        return Profile(
            username = user.username,
            email = user.email,
            profileImage = user.profileImage
        )
    }

    override suspend fun updateUser(
        userId: UUID,
        username: String?,
        email: String?
    ): UserEntity {
        return transaction {
            UserEntity.findById(userId)?.apply {
                username?.let { this.username = it }
                email?.let { this.email = it }
            } ?: throw AppException.NotFoundException("User not found", userId.toString())
        }
    }
}
