package com.plugins

import com.routes.profileRoutes
import com.routes.projectRoutes
import com.routes.taskRoutes
import com.routes.userAuthRoutes
import com.services.ProfileImageService
import com.services.ProjectService
import com.services.TaskService
import com.services.UserAuthService
import com.services.UserProfileService
import io.ktor.server.application.Application
import io.ktor.server.http.content.files
import io.ktor.server.http.content.static
import io.ktor.server.routing.routing

fun Application.configureRouting(
    userAuthService: UserAuthService,
    profileService: UserProfileService,
    profileImageService: ProfileImageService,
    taskService: TaskService,
    projectService: ProjectService
) {
    routing {

        static("/static/profile") {
            files("uploads/profile")
        }

        userAuthRoutes(userAuthService)
        profileRoutes(profileService, profileImageService)
        taskRoutes(taskService)
        projectRoutes(projectService, taskService)
    }
}