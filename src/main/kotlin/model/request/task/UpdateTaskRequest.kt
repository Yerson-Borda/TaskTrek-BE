package com.model.request.task

import com.model.enums.RepeatInterval
import com.utils.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class UpdateTaskRequest(
    val title: String,
    val description: String? = null,
    val pomodoros: Int,

    @Serializable(with = LocalDateTimeSerializer::class)
    val endDate: LocalDateTime,

    @Serializable(with = LocalDateTimeSerializer::class)
    val reminder: LocalDateTime? = null,

    val repeat: RepeatInterval? = null,
    val note: String? = null
)