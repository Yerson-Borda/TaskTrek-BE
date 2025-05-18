package com.model.request.task

import com.model.enums.RepeatInterval
import com.model.enums.TaskTag
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import com.utils.LocalDateTimeSerializer

@Serializable
data class CreateTaskRequest(
    val title: String,
    val description: String? = null,

    @Serializable(with = LocalDateTimeSerializer::class)
    val startDate: LocalDateTime,

    @Serializable(with = LocalDateTimeSerializer::class)
    val endDate: LocalDateTime,

    val pomodoros: Int,
    val tag: TaskTag,
    val repeat: RepeatInterval? = null,

    @Serializable(with = LocalDateTimeSerializer::class)
    val reminder: LocalDateTime? = null,

    val note: String? = null
)
