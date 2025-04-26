package com.dto.response.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse (
    val token: String
)