package com.services

import com.entities.TaskEntity
import com.model.request.task.CreateTaskRequest
import com.model.request.task.UpdateTaskRequest
import com.repository.TaskRepository
import io.ktor.http.content.PartData
import io.ktor.http.content.streamProvider
import java.io.File
import java.util.UUID

interface TaskService {
    suspend fun create(userId: UUID, request: CreateTaskRequest, files: List<PartData.FileItem>): TaskEntity
    suspend fun list(userId: UUID): List<TaskEntity>
    suspend fun get(userId: UUID, taskId: UUID): TaskEntity?
    suspend fun update(userId: UUID, taskId: UUID, request: UpdateTaskRequest): Boolean
    suspend fun delete(userId: UUID, taskId: UUID): Boolean
    suspend fun completePomodoro(taskId: UUID): Boolean
    suspend fun markComplete(userId: UUID, taskId: UUID): Boolean
}

class TaskServiceImpl(
    private val repo: TaskRepository
): TaskService {
    override suspend fun create(userId: UUID, request: CreateTaskRequest, files: List<PartData.FileItem>): TaskEntity {
        val attachments = files.map { file ->
            val name = file.originalFileName ?: "file"
            val uploadPath = "uploads/$name"
            file.streamProvider().use { input ->
                File(uploadPath).outputStream().buffered().use { output ->
                    input.copyTo(output)
                }
            }
            name to uploadPath
        }
        return repo.createTask(userId, request, attachments)
    }

    override suspend fun list(userId: UUID): List<TaskEntity> = repo.getTasks(userId)
    override suspend fun get(userId: UUID, taskId: UUID): TaskEntity? = repo.getTask(userId, taskId)
    override suspend fun update(userId: UUID, taskId: UUID, request: UpdateTaskRequest): Boolean = repo.updateTask(userId, taskId, request)
    override suspend fun delete(userId: UUID, taskId: UUID): Boolean = repo.deleteTask(userId, taskId)
    override suspend fun completePomodoro(taskId: UUID): Boolean = repo.incrementPomodoro(taskId)
    override suspend fun markComplete(userId: UUID, taskId: UUID): Boolean = repo.markComplete(userId, taskId)
}