package com

import com.config.TokenConfigProvider
import com.config.configureAuth
import com.di.appModule
import com.exceptions.configureExceptionHandling
import com.plugins.configureDatabases
import com.plugins.configureHTTP
import com.plugins.configureRouting
import com.plugins.configureSerialization
import com.services.UserAuthService
import io.ktor.server.application.*
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val tokenConfig = TokenConfigProvider.provideTokenConfig(environment.config)
    TokenConfigProvider.initJwtConfig(environment.config)

    install(Koin) {
        modules(appModule)
    }

    val userAuthService by inject<UserAuthService>()

    configureAuth(tokenConfig)
    configureExceptionHandling()
    configureSerialization()
    configureDatabases()
    configureHTTP()
    configureRouting(userAuthService = userAuthService)
}
