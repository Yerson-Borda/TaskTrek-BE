package com.config

import com.model.token.Token
import io.ktor.server.config.*

object TokenConfigProvider {
    fun provideTokenConfig(config: ApplicationConfig): Token {
        val jwt = config.config("jwt")
        return Token(
            issuer = jwt.property("issuer").getString(),
            audience = jwt.property("audience").getString(),
            tokenExpiry = jwt.property("expiration").getString().toLong(),
            secret = jwt.property("secret").getString()
        )
    }

    fun initJwtConfig(config: ApplicationConfig) {
        JwtConfig.init(config)
    }
}