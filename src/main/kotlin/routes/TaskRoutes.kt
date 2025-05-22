package com.routes

import com.mapper.toListItem
import com.mapper.toResponse
import com.model.request.task.CreateTaskRequest
import com.model.request.task.UpdateTaskRequest
import com.services.TaskService
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
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

fun Route.taskRoutes(taskService: TaskService) {
    authenticate("auth_jwt") {
        route("/tasks") {

            get {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = UUID.fromString(principal.payload.getClaim("userId").asString())
                val tasks = taskService.list(userId).map { it.toListItem() }
                call.respond(tasks)
            }

            post {
                val multipart = call.receiveMultipart()
                var request: CreateTaskRequest? = null
                val files = mutableListOf<PartData.FileItem>()

                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FormItem -> {
                            if (part.name == "data") {
                                request = Json.decodeFromString(part.value)
                            }
                        }
                        is PartData.FileItem -> files.add(part)
                        else -> Unit
                    }
                }

                val principal = call.principal<JWTPrincipal>()!!
                val userId = UUID.fromString(principal.payload.getClaim("userId").asString())
                val task = taskService.create(userId, request!!, files)
                call.respond(task.toResponse())
            }

            get("/{id}") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = UUID.fromString(principal.payload.getClaim("userId").asString())
                val id = UUID.fromString(call.parameters["id"]!!)
                taskService.get(userId, id)?.let {
                    call.respond(it.toResponse())
                } ?: call.respond(HttpStatusCode.NotFound)
            }

            put("/{id}") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = UUID.fromString(principal.payload.getClaim("userId").asString())
                val id = UUID.fromString(call.parameters["id"]!!)
                val req = call.receive<UpdateTaskRequest>()
                if (taskService.update(userId, id, req)) call.respond(HttpStatusCode.OK)
                else call.respond(HttpStatusCode.NotFound)
            }

            delete("/{id}") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = UUID.fromString(principal.payload.getClaim("userId").asString())
                val id = UUID.fromString(call.parameters["id"]!!)
                if (taskService.delete(userId, id)) call.respond(HttpStatusCode.OK)
                else call.respond(HttpStatusCode.NotFound)
            }

            post("/{id}/pomodoro") {
                val id = UUID.fromString(call.parameters["id"]!!)
                if (taskService.completePomodoro(id)) call.respond(HttpStatusCode.OK)
                else call.respond(HttpStatusCode.BadRequest)
            }

            post("/{id}/complete") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = UUID.fromString(principal.payload.getClaim("userId").asString())
                val id = UUID.fromString(call.parameters["id"]!!)

                if (taskService.markComplete(userId, id)) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
    }
}