package com.services

import com.model.request.project.CreateProjectRequest
import com.model.request.project.UpdateProjectRequest
import com.model.response.project.ProjectListItemResponse
import com.model.response.project.ProjectResponse
import com.repository.ProjectRepository
import java.util.UUID

interface ProjectService {
    suspend fun list(userId: UUID): List<ProjectListItemResponse>
    suspend fun get(userId: UUID, id: UUID): ProjectResponse?
    suspend fun create(userId: UUID, req: CreateProjectRequest): ProjectResponse
    suspend fun update(userId: UUID, id: UUID, req: UpdateProjectRequest): Boolean
    suspend fun markComplete(userId: UUID, id: UUID): Boolean
    suspend fun delete(userId: UUID, id: UUID): Boolean
    suspend fun join(userId: UUID, code: String): Boolean
}

class ProjectServiceImpl(
    private val repo: ProjectRepository
) : ProjectService {
    override suspend fun list(userId: UUID): List<ProjectListItemResponse> = repo.getAllForUser(userId)
    override suspend fun get(userId: UUID, id: UUID) : ProjectResponse? = repo.getById(userId, id)
    override suspend fun create(userId: UUID, req: CreateProjectRequest): ProjectResponse = repo.create(userId, req)
    override suspend fun update(userId: UUID, id: UUID, req: UpdateProjectRequest) = repo.update(userId, id, req)
    override suspend fun markComplete(userId: UUID, id: UUID) = repo.markComplete(userId, id)
    override suspend fun delete(userId: UUID, id: UUID) = repo.delete(userId, id)
    override suspend fun join(userId: UUID, code: String) = repo.join(userId, code)
}