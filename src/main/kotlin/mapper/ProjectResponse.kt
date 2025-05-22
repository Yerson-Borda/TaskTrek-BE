package com.mapper

import com.entities.ProjectEntity
import com.model.response.project.ProjectResponse

fun ProjectEntity.toResponse(): ProjectResponse {
    return ProjectResponse(
        id = id.value,
        title = title,
        code = code,
        description = description,
        endDate = endDate,
        complete = complete,
        estimatedTime = estimatedTime,
        tasksToComplete = tasksToComplete,
        completedTasks = completedTasks,
        elapsedTime = elapsedTime,
        owner = owner.toUserResponse(),
        members = members.map { it.toUserResponse() }
    )
}