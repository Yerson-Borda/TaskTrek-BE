package com.model.response.project

import com.utils.UUIDSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class UserResponse(
    @Contextual
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val username: String,
    val profileImage: String?
)