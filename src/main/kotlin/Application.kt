package com

import com.config.TokenConfigProvider
import com.config.configureAuth
import com.di.appModule
import com.exceptions.configureExceptionHandling
import com.plugins.configureDatabases
import com.plugins.configureHTTP
import com.plugins.configureRouting
import com.plugins.configureSerialization
import com.services.ProfileImageService
import com.services.UserAuthService
import com.services.UserProfileService
import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    val tokenConfig = TokenConfigProvider.provideTokenConfig(environment.config)

    install(Koin) {
        val appConfig = environment.config
        modules(appModule(appConfig))
    }

    val userAuthService by inject<UserAuthService>()
    val userProfileService by inject<UserProfileService>()
    val profileImageService by inject<ProfileImageService>()

    configureAuth(tokenConfig)
    configureExceptionHandling()
    configureSerialization()
    configureDatabases()
    configureHTTP()
    configureRouting(
        userAuthService = userAuthService,
        profileService = userProfileService,
        profileImageService = profileImageService,
    )
}
