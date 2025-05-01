package com.plugins

import com.routes.userAuthRoutes
import com.services.UserAuthService
import io.ktor.server.application.Application
import io.ktor.server.routing.routing

fun Application.configureRouting(
    userAuthService: UserAuthService,
) {
    routing {
        userAuthRoutes(userAuthService)
    }
}