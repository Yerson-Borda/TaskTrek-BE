package com.mapper

import com.entities.UserEntity
import com.model.response.project.UserResponse

fun UserEntity.toUserResponse(): UserResponse = UserResponse(
    id = id.value,
    username = username,
    profileImage = profileImage
)