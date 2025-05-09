package com.config

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.config.ApplicationConfig
import java.util.Date

class JwtConfig() {

    companion object {
        lateinit var config : ApplicationConfig
        fun init(config : ApplicationConfig) {
            this.config = config
        }
    }

    fun generateToken(username: String) : String {
        return JWT.create()
            .withIssuer(getIssuer())
            .withAudience(getAudience())
            .withClaim("username" , username)
            .withExpiresAt(Date(getExpiration()))
            .sign(Algorithm.HMAC256(getSecret()))
    }

    fun getVerifier() : JWTVerifier {
        return JWT.require(Algorithm.HMAC256(getSecret()))
            .withIssuer(getIssuer())
            .withAudience(getAudience())
            .build()
    }

    fun getIssuer() : String {
        return config.property("jwt.issuer").getString()
    }

    private fun getAudience(): String{
        return config.property("jwt.audience").getString()
    }

    private fun getSecret(): String {
        return config.property("jwt.secret").getString()
    }

    private fun getExpiration(): Long {
        return config.property("jwt.expiration").getString().toLong()
    }
}