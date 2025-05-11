package com.repository

import com.entities.UserEntity
import com.exceptions.AppException
import com.utils.Constants
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.util.UUID

interface ProfileImageRepository {
    suspend fun updateUserProfileImage(userId: UUID, imageUrl: String): Boolean
}

class ProfileImageRepositoryImpl : ProfileImageRepository {
    override suspend fun updateUserProfileImage(userId: UUID, imageUrl: String): Boolean {
        return try {
            transaction {
                val user = UserEntity.findById(userId)
                    ?: throw AppException.BadRequestException("User not found")

                val oldImageUrl = user.profileImage
                if (!oldImageUrl.isNullOrBlank()) {
                    val oldFileName = oldImageUrl.substringAfterLast("/")
                    val file = File("${Constants.STATIC_PROFILE_IMAGE_PATH}/$oldFileName")
                    if (file.exists()) {
                        file.delete()
                    }
                }
                user.profileImage = imageUrl
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}