package com.entities

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.util.UUID

object Projects : UUIDTable("projects") {
    val title = varchar("title", 255)
    val code = varchar("code", 10).uniqueIndex()
    val description = text("description").nullable()
    val endDate = datetime("end_date")
    val complete = bool("complete").default(false)
    val estimatedTime = integer("estimated_time").default(0)
    val tasksToComplete = integer("tasks_to_complete").default(0)
    val completedTasks = integer("completed_tasks").default(0)
    val elapsedTime = integer("elapsed_time").default(0)
    val owner = reference("owner_id", Users)
}

object ProjectMembers : Table("project_members") {
    val project = reference("project_id", Projects)
    val user = reference("user_id", Users)
    init {
        uniqueIndex("project_user_unique", project, user)
    }
}

class ProjectEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ProjectEntity>(Projects)

    var title by Projects.title
    var code by Projects.code
    var description by Projects.description
    var endDate by Projects.endDate
    var complete by Projects.complete
    var estimatedTime by Projects.estimatedTime
    var tasksToComplete by Projects.tasksToComplete
    var completedTasks by Projects.completedTasks
    var elapsedTime by Projects.elapsedTime
    var owner by UserEntity referencedOn Projects.owner
    var members by UserEntity via ProjectMembers
    val tasks get() = TaskEntity.find { Tasks.project eq this@ProjectEntity.id }.toList()
}