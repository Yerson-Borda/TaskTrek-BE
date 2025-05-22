package com.di

import com.config.JwtConfig
import com.config.TokenConfigProvider
import com.model.token.Token
import com.repository.ProfileImageRepository
import com.repository.ProfileImageRepositoryImpl
import com.repository.ProfileRepository
import com.repository.ProfileRepositoryImpl
import com.repository.ProjectRepository
import com.repository.ProjectRepositoryImpl
import com.repository.TaskRepository
import com.repository.TaskRepositoryImpl
import com.repository.TokenBlacklistRepository
import com.repository.TokenBlacklistRepositoryImpl
import com.repository.UserRepository
import com.repository.UserRepositoryImpl
import com.services.ProfileImageService
import com.services.ProfileImageServiceImpl
import com.services.ProjectService
import com.services.ProjectServiceImpl
import com.services.TaskService
import com.services.TaskServiceImpl
import com.services.UserAuthService
import com.services.UserAuthServiceImpl
import com.services.UserProfileService
import com.services.UserProfileServiceImpl
import com.utils.PasswordUtils
import io.ktor.server.config.ApplicationConfig
import org.koin.dsl.module

fun appModule(config: ApplicationConfig) = module {

    single { PasswordUtils() }

    single<Token> { TokenConfigProvider.provideTokenConfig(config) }

    single { JwtConfig(get<Token>()) }

    single<TokenBlacklistRepository> { TokenBlacklistRepositoryImpl() }
    single<ProfileImageRepository> { ProfileImageRepositoryImpl() }
    single<ProfileImageService> { ProfileImageServiceImpl(get()) }
    single<ProfileRepository> { ProfileRepositoryImpl() }
    single<UserProfileService> { UserProfileServiceImpl(get(), get()) }
    single<UserRepository> { UserRepositoryImpl() }
    single<UserAuthService> { UserAuthServiceImpl(get(), get(), get(), get()) }
    single<TaskRepository> { TaskRepositoryImpl() }
    single<TaskService> { TaskServiceImpl(get()) }

    single<ProjectRepository> { ProjectRepositoryImpl() }
    single<ProjectService> { ProjectServiceImpl(get()) }
}