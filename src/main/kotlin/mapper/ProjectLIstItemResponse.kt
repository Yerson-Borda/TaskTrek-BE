package com.mapper

import com.entities.ProjectEntity
import com.model.response.project.ProjectListItemResponse

fun ProjectEntity.toListItemResponse(): ProjectListItemResponse {
    return ProjectListItemResponse(
        id = id.value,
        title = title,
        description = description,
        endDate = endDate,
        tasksToComplete = tasksToComplete,
        completedTasks = completedTasks,
        members = members.map { it.toUserResponse() }
    )
}