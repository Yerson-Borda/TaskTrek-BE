package com.services

import com.repository.ProfileImageRepository
import com.utils.Constants
import com.utils.save
import io.ktor.http.content.PartData
import java.io.File
import java.util.UUID

interface ProfileImageService {
    suspend fun updateUserProfileImage(userId: UUID, file: PartData.FileItem): Boolean
}

class ProfileImageServiceImpl(
    private val repository: ProfileImageRepository
) : ProfileImageService {
    override suspend fun updateUserProfileImage(userId: UUID, file: PartData.FileItem): Boolean {
        var fileName: String? = null
        return try {
            fileName = file.save(Constants.STATIC_PROFILE_IMAGE_PATH)
            val imageUrl = "${Constants.BASE_URL}${Constants.EXTERNAL_PROFILE_IMAGE_PATH}/$fileName"
            val updated = repository.updateUserProfileImage(userId, imageUrl)
            if (!updated) {
                File("${Constants.STATIC_PROFILE_IMAGE_PATH}/$fileName").delete()
            }
            updated
        } catch (e: Exception) {
            if (fileName != null) {
                File("${Constants.STATIC_PROFILE_IMAGE_PATH}/$fileName").delete()
            }
            false
        }
    }
}