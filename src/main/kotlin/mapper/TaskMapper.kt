package com.mapper

import com.entities.TaskEntity
import com.model.response.task.AttachmentResponse
import com.model.response.task.TaskResponse

fun TaskEntity.toResponse(): TaskResponse {
    return TaskResponse(
        id = this.id.value,
        title = this.title,
        description = this.description,
        startDate = this.startDate,
        endDate = this.endDate,
        pomodoros = this.targetPomodoros,
        tag = this.tag,
        repeat = this.repeat,
        reminder = this.reminder,
        note = this.note,
        complete = this.complete
//        attachments = this.attachments.map {
//            AttachmentResponse(
//                filename = it.filename,
//                url = it.fileUrl
//            )
//        }
    )
}