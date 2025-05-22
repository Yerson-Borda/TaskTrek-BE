package com.routes

import com.model.request.project.CreateProjectRequest
import com.model.request.project.JoinProjectRequest
import com.model.request.project.UpdateProjectRequest
import com.model.request.task.CreateTaskRequest
import com.services.ProjectService
import com.services.TaskService
import com.utils.getUserId
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import kotlinx.serialization.json.Json
import java.util.UUID

fun Route.projectRoutes(
    projectService: ProjectService,
    taskService: TaskService,
) {
    authenticate("auth_jwt") {
        route("/projects") {
            get {
                val userId = getUserId(call)
                val projects = projectService.list(userId)
                call.respond(projects) // Already ProjectListItemResponse
            }

            post {
                val userId = getUserId(call)
                val req = call.receive<CreateProjectRequest>()
                val project = projectService.create(userId, req)
                call.respond(project) // Already a ProjectResponse
            }

            post("/{id}/tasks") {
                val userId = getUserId(call)
                val projectId = UUID.fromString(call.parameters["id"]!!)
                val multipart = call.receiveMultipart()
                var taskRequest: CreateTaskRequest? = null
                val files = mutableListOf<PartData.FileItem>()

                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FormItem -> {
                            if (part.name == "task") {
                                taskRequest = Json.decodeFromString(CreateTaskRequest.serializer(), part.value)
                            }
                        }
                        is PartData.FileItem -> files.add(part)
                        else -> part.dispose()
                    }
                }

                val requestWithProject = taskRequest?.copy(projectId = projectId)

                if (requestWithProject != null) {
                    val task = taskService.create(userId, requestWithProject, files)
                    call.respond(HttpStatusCode.Created, mapOf("id" to task.id.value))
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Missing task data")
                }
            }

            post("/join") {
                val userId = getUserId(call)
                val req = call.receive<JoinProjectRequest>()
                if (projectService.join(userId, req.code)) call.respond(HttpStatusCode.OK)
                else call.respond(HttpStatusCode.BadRequest)
            }

            get("/{id}") {
                val userId = getUserId(call)
                val id = UUID.fromString(call.parameters["id"]!!)
                projectService.get(userId, id)?.let {
                    call.respond(it)
                } ?: call.respond(HttpStatusCode.NotFound)
            }

            put("/{id}") {
                val userId = getUserId(call)
                val id = UUID.fromString(call.parameters["id"]!!)
                val req = call.receive<UpdateProjectRequest>()
                if (projectService.update(userId, id, req)) call.respond(HttpStatusCode.OK)
                else call.respond(HttpStatusCode.Forbidden)
            }

            post("/{id}/complete") {
                val userId = getUserId(call)
                val id = UUID.fromString(call.parameters["id"]!!)
                if (projectService.markComplete(userId, id)) call.respond(HttpStatusCode.OK)
                else call.respond(HttpStatusCode.Forbidden)
            }

            delete("/{id}") {
                val userId = getUserId(call)
                val id = UUID.fromString(call.parameters["id"]!!)
                if (projectService.delete(userId, id)) call.respond(HttpStatusCode.OK)
                else call.respond(HttpStatusCode.Forbidden)
            }
        }
    }
}