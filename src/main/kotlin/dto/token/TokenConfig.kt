package com.dto.token

data class TokenConfig(
    val secret: String,
    val issuer: String,
    val audience: String,
    val tokenExpiry: Long
)