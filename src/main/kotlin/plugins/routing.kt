package com.plugins

import com.routes.profileRoutes
import com.routes.userAuthRoutes
import com.services.UserAuthService
import com.services.UserProfileService
import io.ktor.server.application.Application
import io.ktor.server.routing.routing

fun Application.configureRouting(
    userAuthService: UserAuthService,
    profileService: UserProfileService
) {
    routing {
        userAuthRoutes(userAuthService)
        profileRoutes(profileService)
    }
}