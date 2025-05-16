package com.services

import com.model.request.auth.LoginRequest
import com.model.request.auth.RegisterRequest
import com.repository.UserRepository
import com.utils.PasswordUtils
import com.exceptions.AppException
import com.config.JwtConfig
import com.repository.TokenBlacklistRepository
import io.ktor.server.auth.jwt.JWTPrincipal
import java.util.UUID

interface UserAuthService {
    suspend fun register(registerRequest: RegisterRequest): Boolean
    suspend fun login(loginRequest: LoginRequest): String
    suspend fun logout(token: String, principal: JWTPrincipal)
}

class UserAuthServiceImpl(
    private val userRepository: UserRepository,
    private val passwordUtil: PasswordUtils,
    private val jwtConfig: JwtConfig,
    private val blacklistRepo: TokenBlacklistRepository
): UserAuthService {

    override suspend fun register(registerRequest: RegisterRequest): Boolean {
        if (userRepository.findByEmail(registerRequest.email) != null) {
            throw AppException.ConflictException("Email already registered")
        }
        return userRepository.createUser(
            username = registerRequest.username,
            email = registerRequest.email,
            password = passwordUtil.hash(password = registerRequest.password),
        )
    }

    override suspend fun login(loginRequest: LoginRequest): String {
        val user = userRepository.findByEmail(loginRequest.email)
            ?: throw AppException.UnauthorizedException("Invalid Credentials")

        if (!passwordUtil.verify(password = loginRequest.password, hash = user.password)) {
            throw AppException.UnauthorizedException("Invalid Credentials")
        }

        return jwtConfig.generateToken(user.id.value)
    }

    override suspend fun logout(token: String, principal: JWTPrincipal) {
        val userId = UUID.fromString(principal.payload.getClaim("userId").asString())
        val expiresAt = principal.expiresAt?.toInstant() ?: throw AppException.BadRequestException("Invalid token")
        blacklistRepo.blacklistToken(token, userId, expiresAt)
    }
}