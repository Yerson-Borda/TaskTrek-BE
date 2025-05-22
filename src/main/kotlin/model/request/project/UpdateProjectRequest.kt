package com.model.request.project

import com.utils.LocalDateTimeSerializer
import java.time.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class UpdateProjectRequest(
    val title: String,
    val description: String?,
    @Serializable(with = LocalDateTimeSerializer::class)
    val endDate: LocalDateTime,
)