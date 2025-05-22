package com.repository

import com.entities.ProjectEntity
import com.entities.Projects
import com.entities.TaskEntity
import com.entities.Tasks
import com.entities.UserEntity
import com.mapper.toListItemResponse
import com.mapper.toResponse
import com.model.request.project.CreateProjectRequest
import com.model.request.project.UpdateProjectRequest
import com.model.response.project.ProjectListItemResponse
import com.model.response.project.ProjectResponse
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

interface ProjectRepository {
    suspend fun getAllForUser(userId: UUID): List<ProjectListItemResponse>
    suspend fun getById(userId: UUID, projectId: UUID): ProjectResponse?
    suspend fun create(userId: UUID, request: CreateProjectRequest): ProjectResponse
    suspend fun update(userId: UUID, projectId: UUID, request: UpdateProjectRequest): Boolean
    suspend fun markComplete(userId: UUID, projectId: UUID): Boolean
    suspend fun delete(userId: UUID, projectId: UUID): Boolean
    suspend fun join(userId: UUID, code: String): Boolean
}

class ProjectRepositoryImpl : ProjectRepository {
    override suspend fun getAllForUser(userId: UUID): List<ProjectListItemResponse> = transaction {
        val user = UserEntity[userId]
        (user.ownedProjects + user.memberProjects)
            .distinct()
            .map { it.toListItemResponse() }
    }

    override suspend fun getById(userId: UUID, projectId: UUID): ProjectResponse? = transaction {
        ProjectEntity.findById(projectId)?.takeIf {
            it.owner.id.value == userId || it.members.any { member -> member.id.value == userId }
        }?.toResponse()
    }

    override suspend fun create(userId: UUID, request: CreateProjectRequest): ProjectResponse = transaction {
        val code = generateRandomCode()
        val user = UserEntity[userId]
        val project = ProjectEntity.new {
            this.title = request.title
            this.code = code
            this.endDate = request.endDate
            this.owner = user
        }

        project.members = SizedCollection(listOf(user))
        project.toResponse()
    }

    override suspend fun update(userId: UUID, projectId: UUID, request: UpdateProjectRequest): Boolean = transaction {
        val project = ProjectEntity.findById(projectId)?.takeIf { it.owner.id.value == userId } ?: return@transaction false
        project.title = request.title
        project.description = request.description
        project.endDate = request.endDate
        true
    }

    override suspend fun markComplete(userId: UUID, projectId: UUID): Boolean = transaction {
        val project = ProjectEntity.findById(projectId)?.takeIf { it.owner.id.value == userId } ?: return@transaction false
        project.complete = true
        true
    }

    override suspend fun delete(userId: UUID, projectId: UUID): Boolean = transaction {
        val project = ProjectEntity.findById(projectId)?.takeIf { it.owner.id.value == userId } ?: return@transaction false

        TaskEntity.find { Tasks.project eq project.id }.forEach { it.delete() }

        project.members = SizedCollection(emptyList())

        project.delete()
        true
    }

    override suspend fun join(userId: UUID, code: String): Boolean = transaction {
        val project = ProjectEntity.find { Projects.code eq code }.firstOrNull() ?: return@transaction false
        val user = UserEntity[userId]
        if (!project.members.contains(user)) {
            project.members = SizedCollection(project.members + user)
        }
        true
    }

    private fun generateRandomCode(length: Int = 8): String {
        val allowedChars = ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }
}