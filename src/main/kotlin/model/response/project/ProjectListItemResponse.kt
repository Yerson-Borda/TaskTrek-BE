package com.model.response.project

import kotlinx.serialization.Serializable
import com.utils.LocalDateTimeSerializer
import com.utils.UUIDSerializer
import kotlinx.serialization.Contextual
import java.time.LocalDateTime
import java.util.UUID

@Serializable
data class ProjectListItemResponse(
    @Contextual
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val title: String,
    val description: String?,
    @Serializable(with = LocalDateTimeSerializer::class)
    val endDate: LocalDateTime,
    val tasksToComplete: Int,
    val completedTasks: Int,
    val members: List<UserResponse>
)