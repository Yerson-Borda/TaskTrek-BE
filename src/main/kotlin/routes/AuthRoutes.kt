package com.routes

import com.model.request.auth.LoginRequest
import com.model.request.auth.RegisterRequest
import com.model.response.auth.AuthResponse
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.services.UserAuthService
import io.ktor.http.HttpStatusCode
import com.exceptions.AppException

fun Route.userAuthRoutes(
    authService: UserAuthService
) {
    route("/auth") {
        post("/register") {
            val request = call.receive<RegisterRequest>()
            val success = authService.register(request)
            if (success) {
                call.respond(
                    status =  HttpStatusCode.OK ,
                    message = "Registration Successful"
                )
            } else {
                throw AppException.InternalServerError()
            }
        }
        post("/login") {
            val credentials = call.receive<LoginRequest>()
            try {
                val token = authService.login(credentials)
                call.respond(
                    status = HttpStatusCode.OK,
                    message = AuthResponse(
                        token
                    )
                )
            } catch (e : AppException.UnauthorizedException) {
                call.respond(
                    status = HttpStatusCode.Unauthorized,
                    message = mapOf("code" to e.code, "message" to e.message)
                )
            }catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("code" to "INTERNAL_ERROR", "message" to "An unexpected error occurred"))
            }
        }
    }
}