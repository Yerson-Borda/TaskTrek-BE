package com.exceptions

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

open class AppException(
    val code : String,
    message : String
) : RuntimeException(message) {
    //400
    class BadRequestException(message: String) : AppException("BAD_REQUEST" , message)
    class InvalidCredentialsException : AppException("INVALID_CREDENTIALS" , "Invalid email or password")

    //401
    class UnauthorizedException(message: String) : AppException("UNAUTHORIZED" , message)
    //403
    class ForbiddenException : AppException("FORBIDDEN" , "Insufficient permission")
    //404
    class NotFoundException(resource : String , id : Any?) : AppException("NOT_FOUND" , "$resource ${id?: ""} not found")
    //409
    class ConflictException(message: String) :AppException("CONFLICT" , message)
    //500
    class InternalServerError : AppException("INTERNAL_ERROR" , "An unexpected error occurred")
}

fun Application.configureExceptionHandling() {
    install(StatusPages) {
        exception<AppException> { call , cause ->
            when(cause) {
                is AppException.BadRequestException -> call.respond(HttpStatusCode.BadRequest , cause.toResponse())
                is AppException.UnauthorizedException -> call.respond(HttpStatusCode.Unauthorized , cause.toResponse())
                is AppException.ForbiddenException -> call.respond(HttpStatusCode.Forbidden , cause.toResponse())
                is AppException.NotFoundException -> call.respond(HttpStatusCode.NotFound , cause.toResponse())
                is AppException.ConflictException -> call.respond(HttpStatusCode.Conflict , cause.toResponse())
                is AppException.InternalServerError -> call.respond(HttpStatusCode.InternalServerError , cause.toResponse())
            }
        }
    }
}

fun AppException.toResponse() = mapOf(
    "code" to this.code,
    "message" to this.message
)