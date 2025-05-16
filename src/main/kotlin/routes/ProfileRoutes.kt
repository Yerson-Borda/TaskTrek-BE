package com.routes

import com.exceptions.AppException
import com.model.request.profile.UpdateProfile
import com.model.response.profile.Profile
import com.services.ProfileImageService
import com.services.UserProfileService
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
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import java.util.UUID

fun Route.profileRoutes(
    profileService: UserProfileService,
    profileImageService: ProfileImageService
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

            post("/image") {
                val userIdStr = call.principal<JWTPrincipal>()?.getClaim("userId", String::class)
                    ?: throw AppException.UnauthorizedException("Invalid token")
                val userId = UUID.fromString(userIdStr)

                val multipart = call.receiveMultipart()
                try {
                    var success = false

                    multipart.forEachPart { part ->
                        if (part is PartData.FileItem) {
                            success = profileImageService.updateUserProfileImage(userId, part)
                            part.dispose()
                        } else {
                            part.dispose()
                        }
                    }

                    if (success) {
                        call.respond(HttpStatusCode.OK, "Updated Successfully")
                    } else {
                        throw AppException.BadRequestException("No valid image found")
                    }
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