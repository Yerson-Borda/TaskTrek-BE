package com.services

import com.entities.UserEntity
import com.repository.UserRepository
import com.exceptions.AppException
import com.repository.ProfileRepository
import java.util.UUID

interface UserProfileService {
    suspend fun getProfile(userId: UUID): UserEntity
    suspend fun updateProfile(
        userId: UUID,
        username: String?,
        email: String?
    ): UserEntity
    suspend fun getUserIdByEmail(email: String): UUID
}

class UserProfileServiceImpl(
    private val profileRepository: ProfileRepository,
    private val userRepository: UserRepository
) : UserProfileService {

    override suspend fun getProfile(userId: UUID): UserEntity {
        return userRepository.findById(userId)
            ?: throw AppException.NotFoundException("User not found", userId)
    }

    override suspend fun updateProfile(
        userId: UUID,
        username: String?,
        email: String?
    ): UserEntity {
        return profileRepository.updateUser(
            userId = userId,
            username = username,
            email = email
        )
    }

    override suspend fun getUserIdByEmail(email: String): UUID {
        return userRepository.findByEmail(email)?.id?.value
            ?: throw AppException.NotFoundException("User not found", email)
    }

}
