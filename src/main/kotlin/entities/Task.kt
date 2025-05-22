package com.entities

import com.model.enums.TaskTag
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.util.UUID
import com.model.enums.RepeatInterval

object Tasks : UUIDTable("tasks") {
    val user = reference("user_id", Users)
    val tag = enumerationByName("tag", 20, TaskTag::class)
    val title = varchar("title", 255)
    val description = text("description").nullable()
    val startDate = datetime("start_date")
    val endDate = datetime("end_date")
    val completedPomodoros = integer("completed_pomodoros").default(0)
    val targetPomodoros = integer("target_pomodoros")
    val reminder = datetime("reminder").nullable()
    val repeat = enumerationByName("repeat", 20, RepeatInterval::class).nullable()
    val note = text("note").nullable()
    val complete = bool("complete").default(false)
    val project = reference("project_id", Projects).nullable()
}

class TaskEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<TaskEntity>(Tasks)

    var user by UserEntity referencedOn Tasks.user
    var project by ProjectEntity optionalReferencedOn Tasks.project
    var tag by Tasks.tag
    var title by Tasks.title
    var description by Tasks.description
    var startDate by Tasks.startDate
    var endDate by Tasks.endDate
    var completedPomodoros by Tasks.completedPomodoros
    var targetPomodoros by Tasks.targetPomodoros
    var reminder by Tasks.reminder
    var repeat by Tasks.repeat
    var note by Tasks.note
    val attachments by TaskAttachmentEntity referrersOn TaskAttachments.task
    var complete by Tasks.complete
}