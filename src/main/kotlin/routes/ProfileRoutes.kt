package com.routes

import com.exceptions.AppException
import com.model.request.profile.UpdateProfile
import com.model.response.profile.Profile
import com.repository.UserRepository
import com.services.UserProfileService
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import java.util.UUID

fun Route.profileRoutes(
    profileService: UserProfileService
) {
    authenticate("auth_jwt") {
        route("/profile") {
            get {
                val userIdStr = call.principal<JWTPrincipal>()?.getClaim("userId", String::class)
                    ?: throw AppException.UnauthorizedException("Invalid token")

                val userId = UUID.fromString(userIdStr)

                val profile = profileService.getProfile(userId)
                call.respond(Profile.fromDomain(profile))
            }

            put {
                val userIdStr = call.principal<JWTPrincipal>()?.getClaim("userId", String::class)
                    ?: throw AppException.UnauthorizedException("Invalid token")

                val userId = UUID.fromString(userIdStr)

                val request = call.receive<UpdateProfile>()
                val updatedProfile = profileService.updateProfile(
                    userId = userId,
                    username = request.username,
                    email = request.email
                )

                call.respond(Profile.fromDomain(updatedProfile))
            }

        }
    }
}