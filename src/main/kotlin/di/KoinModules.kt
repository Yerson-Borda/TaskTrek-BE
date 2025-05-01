package com.di

import com.config.JwtConfig
import com.repository.UserRepository
import com.repository.UserRepositoryImpl
import com.services.UserAuthService
import com.services.UserAuthServiceImpl
import com.utils.PasswordUtils
import org.koin.dsl.module

val appModule = module {

    single { PasswordUtils() }
    single { JwtConfig() }

//    single<Token> { TokenConfigProvider.provideTokenConfig(get<ApplicationConfig>()) }

    single<UserRepository> { UserRepositoryImpl() }
    single<UserAuthService> { UserAuthServiceImpl(get(), get(), get()) }
}