package com

import com.config.configureAuth
import com.di.appModule
import com.exceptions.configureExceptionHandling
import com.middlewares.installTokenBlacklistCheck
import com.model.token.Token
import com.plugins.configureDatabases
import com.plugins.configureHTTP
import com.plugins.configureRouting
import com.plugins.configureSerialization
import com.repository.TokenBlacklistRepository
import com.services.ProfileImageService
import com.services.ProjectService
import com.services.TaskService
import com.services.UserAuthService
import com.services.UserProfileService
import com.utils.scheduleTokenCleanup
import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    install(Koin) {
        val appConfig = environment.config
        modules(appModule(appConfig))
    }

    val userAuthService by inject<UserAuthService>()
    val userProfileService by inject<UserProfileService>()
    val profileImageService by inject<ProfileImageService>()
    val tokenBlacklistRepo by inject<TokenBlacklistRepository>()
    val tokenConfig by inject<Token>()
    val taskService by inject<TaskService>()
    val projectService by inject<ProjectService>()

    configureAuth(tokenConfig)
    installTokenBlacklistCheck(tokenBlacklistRepo)
    scheduleTokenCleanup(tokenBlacklistRepo)

    configureExceptionHandling()
    configureSerialization()
    configureDatabases()
    configureHTTP()
    configureRouting(
        userAuthService = userAuthService,
        profileService = userProfileService,
        profileImageService = profileImageService,
        taskService = taskService,
        projectService = projectService
    )
}