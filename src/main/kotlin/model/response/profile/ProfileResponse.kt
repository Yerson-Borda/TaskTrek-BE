package com.model.response.profile

import com.entities.UserEntity
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val username: String,
    val email: String,
    val profileImage: String?
) {
    companion object {
        fun fromDomain(user: UserEntity): Profile {
            return Profile(
                username = user.username,
                email = user.email,
                profileImage = user.profileImage
            )
        }
    }
}