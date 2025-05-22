package com.repository

import com.entities.ProjectEntity
import com.entities.TaskAttachmentEntity
import com.entities.TaskAttachments
import com.entities.TaskEntity
import com.entities.Tasks
import com.entities.UserEntity
import com.model.request.task.CreateTaskRequest
import com.model.request.task.UpdateTaskRequest
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

interface TaskRepository {
    suspend fun createTask(userId: UUID, request: CreateTaskRequest, attachments: List<Pair<String, String>>): TaskEntity
    suspend fun getTasks(userId: UUID): List<TaskEntity>
    suspend fun getTask(userId: UUID, taskId: UUID): TaskEntity?
    suspend fun updateTask(userId: UUID, taskId: UUID, request: UpdateTaskRequest): Boolean
    suspend fun deleteTask(userId: UUID, taskId: UUID): Boolean
    suspend fun incrementPomodoro(taskId: UUID): Boolean
    suspend fun markComplete(userId: UUID, taskId: UUID): Boolean
}

class TaskRepositoryImpl : TaskRepository {
    override suspend fun createTask(userId: UUID, request: CreateTaskRequest, attachments: List<Pair<String, String>>): TaskEntity = transaction {
        val task = TaskEntity.new {
            this.user = UserEntity[userId]
            this.title = request.title
            this.description = request.description
            this.startDate = request.startDate
            this.endDate = request.endDate
            this.tag = request.tag
            this.targetPomodoros = request.pomodoros
            this.reminder = request.reminder
            this.repeat = request.repeat
            this.note = request.note
            this.project = request.projectId?.let { ProjectEntity.findById(it) }
        }

        attachments.forEach { (filename, url) ->
            TaskAttachmentEntity.new {
                this.task = task
                this.filename = filename
                this.fileUrl = url
            }
        }

        request.projectId?.let { projectId ->
            val project = ProjectEntity.findById(projectId)
            project?.let {
                val incompleteCount = it.tasks.count { task -> !task.complete }
                val completeCount = it.tasks.count { task -> task.complete }
                it.tasksToComplete = incompleteCount
                it.completedTasks = completeCount
            }
        }

        task
    }


    override suspend fun getTasks(userId: UUID): List<TaskEntity> = transaction {
        TaskEntity.find { Tasks.user eq userId }.toList()
    }

    override suspend fun getTask(userId: UUID, taskId: UUID): TaskEntity? = transaction {
        TaskEntity.findById(taskId)?.takeIf { it.user.id.value == userId }
    }

    override suspend fun updateTask(userId: UUID, taskId: UUID, request: UpdateTaskRequest): Boolean = transaction {
        val task = TaskEntity.findById(taskId)?.takeIf { it.user.id.value == userId } ?: return@transaction false
        task.title = request.title
        task.description = request.description
        task.endDate = request.endDate
        task.targetPomodoros = request.pomodoros
        task.reminder = request.reminder
        task.repeat = request.repeat
        task.note = request.note
        true
    }

    override suspend fun deleteTask(userId: UUID, taskId: UUID): Boolean = transaction {
        val task = TaskEntity.findById(taskId)?.takeIf { it.user.id.value == userId } ?: return@transaction false

        TaskAttachmentEntity.find { TaskAttachments.task eq task.id }.forEach { it.delete() }

        task.delete()
        true
    }

    override suspend fun incrementPomodoro(taskId: UUID): Boolean = transaction {
        val task = TaskEntity.findById(taskId) ?: return@transaction false
        task.completedPomodoros++
        true
    }

    override suspend fun markComplete(userId: UUID, taskId: UUID): Boolean = transaction {
        val task = TaskEntity.findById(taskId)?.takeIf { it.user.id.value == userId } ?: return@transaction false
        if (!task.complete) {
            task.complete = true
            task.project?.let { project ->
                project.tasksToComplete = project.tasks.count { !it.complete }
                project.completedTasks = project.tasks.count { it.complete }
            }
        }
        true
    }
}