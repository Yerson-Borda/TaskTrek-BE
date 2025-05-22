package com.model.request.project

import kotlinx.serialization.Serializable

@Serializable
data class JoinProjectRequest(
    val code: String
)