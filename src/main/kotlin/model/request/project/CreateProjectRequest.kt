package com.model.request.project

import kotlinx.serialization.Serializable
import com.utils.LocalDateTimeSerializer
import java.time.LocalDateTime

@Serializable
data class CreateProjectRequest(
    val title: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val endDate: LocalDateTime
)
