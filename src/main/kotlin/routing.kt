package com

import com.routes.userAuthRoutes
import com.services.UserAuthService
import io.ktor.client.HttpClient
import io.ktor.server.application.Application
import io.ktor.server.routing.routing


fun Application.configureRouting(
    userAuthService: UserAuthService,
    httpClient: HttpClient
) {
    routing {
        userAuthRoutes(userAuthService, httpClient)
    }
}