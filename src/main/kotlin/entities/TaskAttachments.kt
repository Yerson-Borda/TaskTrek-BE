package com.entities

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.util.UUID
import java.time.Instant

object TaskAttachments : UUIDTable("task_attachments") {
    val task = reference("task_id", Tasks)
    val filename = varchar("filename", 255)
    val fileUrl = varchar("file_url", 512)
    val uploadedAt = timestamp("uploaded_at").clientDefault { Instant.now() }
}

class TaskAttachmentEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<TaskAttachmentEntity>(TaskAttachments)

    var task by TaskEntity referencedOn TaskAttachments.task
    var filename by TaskAttachments.filename
    var fileUrl by TaskAttachments.fileUrl
    var uploadedAt by TaskAttachments.uploadedAt
}