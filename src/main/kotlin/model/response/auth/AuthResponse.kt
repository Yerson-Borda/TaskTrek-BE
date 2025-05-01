package com.model.response.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse (
    val token: String
)