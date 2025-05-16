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
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal

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
            }catch (e: AppException.InternalServerError) {
                call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = mapOf("code" to e.code, "message" to e.message)
                )
            }
        }

        authenticate("auth_jwt") {
            post("/logout") {
                val principal = call.principal<JWTPrincipal>()!!
                val token = call.request.headers["Authorization"]
                    ?.removePrefix("Bearer ")
                    ?.trim()
                    ?: return@post call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = "Missing token"
                    )

                try {
                    authService.logout(token, principal)
                    call.respond(
                        status = HttpStatusCode.OK,
                        message = mapOf("message" to "Logged out successfully")
                    )
                } catch (e: AppException.InternalServerError) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = mapOf("code" to e.code, "message" to e.message)
                    )
                }
            }
        }
    }
}