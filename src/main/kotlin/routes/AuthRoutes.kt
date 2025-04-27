package com.routes

import com.dto.request.auth.LoginRequest
import com.dto.request.auth.RegisterRequest
import com.dto.response.auth.AuthResponse
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.services.UserAuthService
import io.ktor.http.HttpStatusCode
import com.exceptions.AppException
import com.utils.GoogleTokenRequest
import com.utils.UserInfo
import com.utils.fetchGoogleUserInfo
import io.ktor.client.HttpClient

fun Route.userAuthRoutes(
    authService: UserAuthService,
    httpClient: HttpClient
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

        post("/google") {
            val request = call.receive<GoogleTokenRequest>()
            println("Received token: ${request.token.take(10)}...")
            try {
                val googleUser = fetchGoogleUserInfo(httpClient, request.token)
                    ?: run {
                        println("Failed to fetch user info - returning 401")
                        call.respond(HttpStatusCode.Unauthorized)
                        return@post
                    }

                val token = authService.loginWithGoogle(
                    UserInfo(
                        name = googleUser.name,
                        email = googleUser.email
                    )
                )

                call.respond(
                    status = HttpStatusCode.OK,
                    message = AuthResponse(token)
                )
            } catch (e: AppException.UnauthorizedException) {
                call.respond(
                    status = HttpStatusCode.Unauthorized,
                    message = mapOf("code" to e.code, "message" to e.message)
                )
            } catch (e: Exception) {
                call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = mapOf("code" to "INTERNAL_ERROR", "message" to "Google authentication failed")
                )
            }
        }
    }
}



















