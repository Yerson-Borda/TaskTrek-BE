package com.model.response.project

import com.utils.UUIDSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID
import com.utils.LocalDateTimeSerializer
import java.time.LocalDateTime

@Serializable
data class ProjectResponse(
    @Contextual
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,

    val title: String,
    val code: String,
    val description: String?,

    @Serializable(with = LocalDateTimeSerializer::class)
    val endDate: LocalDateTime,
    val complete: Boolean,
    val estimatedTime: Int,
    val tasksToComplete: Int,
    val completedTasks: Int,
    val elapsedTime: Int,
    val owner: UserResponse,
    val members: List<UserResponse>
)