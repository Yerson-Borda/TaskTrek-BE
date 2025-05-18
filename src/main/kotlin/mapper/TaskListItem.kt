package com.mapper

import com.entities.TaskEntity
import com.model.response.task.TaskListItemResponse

fun TaskEntity.toListItem(): TaskListItemResponse = TaskListItemResponse(
    title = this.title,
    endDate = this.endDate,
    complete = this.complete
)