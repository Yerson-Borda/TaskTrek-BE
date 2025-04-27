package com.services

import com.dto.request.auth.LoginRequest
import com.dto.request.auth.RegisterRequest
import com.repository.UserRepository
import com.utils.PasswordUtils
import com.exceptions.AppException
import com.utils.JwtConfig
import com.utils.UserInfo
import java.util.UUID

interface UserAuthService {
    suspend fun register(registerRequest: RegisterRequest): Boolean
    suspend fun login(loginRequest: LoginRequest): String
    suspend fun loginWithGoogle(userInfo: UserInfo): String
}

class UserAuthServiceImpl(
    private val userRepository: UserRepository,
    private val passwordUtil: PasswordUtils,
    private val jwtConfig: JwtConfig
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
        if(!passwordUtil.verify(password = loginRequest.password , hash = user.password)) {
            throw AppException.UnauthorizedException("Invalid Credentials")
        }
        return jwtConfig.generateToken(user.username)
    }

    override suspend fun loginWithGoogle(userInfo: UserInfo): String {
        val user = userRepository.findByEmail(userInfo.email) ?: run {
            // Create a user if it doesn't exist
            val username = userInfo.name.replace(" ", "").take(12)
            val success = userRepository.createUser(
                username = username,
                email = userInfo.email,
                password = passwordUtil.hash(UUID.randomUUID().toString()), // Random password for Google users
                isGoogleUser = true
            )
            if (!success) throw AppException.InternalServerError()
            userRepository.findByEmail(userInfo.email) ?: throw AppException.NotFoundException("User not found", 1)
        }

        return jwtConfig.generateToken(user.username)
    }
}