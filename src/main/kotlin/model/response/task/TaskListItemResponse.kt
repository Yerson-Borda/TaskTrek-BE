package com.model.response.task

import com.utils.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class TaskListItemResponse(
    val title: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val endDate: LocalDateTime,
    val complete: Boolean
)