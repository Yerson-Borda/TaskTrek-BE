package com.model.response.task

import com.model.enums.RepeatInterval
import com.model.enums.TaskTag
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import com.utils.LocalDateTimeSerializer
import java.util.UUID
import kotlinx.serialization.Contextual
import com.utils.UUIDSerializer

@Serializable
data class TaskResponse(
    @Contextual
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val title: String,
    val description: String?,
    @Serializable(with = LocalDateTimeSerializer::class)
    val startDate: LocalDateTime,
    @Serializable(with = LocalDateTimeSerializer::class)
    val endDate: LocalDateTime,
    val pomodoros: Int,
    val tag: TaskTag,
    val repeat: RepeatInterval?,
    @Serializable(with = LocalDateTimeSerializer::class)
    val reminder: LocalDateTime?,
    val note: String?,
//    val attachments: List<AttachmentResponse> = emptyList(),
    val complete: Boolean
)