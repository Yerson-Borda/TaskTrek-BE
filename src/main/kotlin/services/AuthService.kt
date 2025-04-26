package com.services

import com.dto.request.auth.LoginRequest
import com.dto.request.auth.RegisterRequest
import com.repository.UserRepository
import com.utils.PasswordUtils
import com.exceptions.AppException
import com.utils.JwtConfig

interface UserAuthService {
    suspend fun register(registerRequest: RegisterRequest): Boolean
    suspend fun login(loginRequest: LoginRequest): String
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
}