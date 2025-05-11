package com.model.request.profile

import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfile(
    val username: String?,
    val email: String?
)