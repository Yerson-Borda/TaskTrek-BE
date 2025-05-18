package com.model.response.task

import kotlinx.serialization.Serializable

@Serializable
data class AttachmentResponse(
    val filename: String,
    val url: String
)
