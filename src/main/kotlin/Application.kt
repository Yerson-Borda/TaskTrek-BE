package com

import com.configuration.configureAuth
import com.di.appModule
import com.dto.token.TokenConfig
import com.exceptions.configureExceptionHandling
import com.services.UserAuthService
import com.utils.JwtConfig
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val httpClient = HttpClient(CIO) {
        install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
            json()
        }
    }

    val jwt = environment.config.config("jwt")

    val tokenConfig = TokenConfig(
        issuer = jwt.property("issuer").getString(),
        audience = jwt.property("audience").getString(),
        tokenExpiry = jwt.property("expiry").getString().toLong(),
        secret = jwt.property("secret").getString()
    )
    JwtConfig.init(environment.config)

    install(Koin) {
        modules(appModule)
    }

    val userAuthService by inject<UserAuthService>()

    configureAuth(tokenConfig, httpClient)
    configureExceptionHandling()
    configureSerialization()
    configureDatabases()
    configureHTTP()
    configureRouting(
        userAuthService = userAuthService,
        httpClient = httpClient
    )
}