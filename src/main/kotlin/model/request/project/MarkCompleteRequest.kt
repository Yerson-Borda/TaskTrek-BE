package com.model.request.project

import kotlinx.serialization.Serializable

@Serializable
data class MarkCompleteRequest(
    val complete: Boolean
)