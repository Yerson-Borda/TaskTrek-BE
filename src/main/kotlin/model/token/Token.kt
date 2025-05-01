package com.model.token

data class Token(
    val secret: String,
    val issuer: String,
    val audience: String,
    val tokenExpiry: Long
)