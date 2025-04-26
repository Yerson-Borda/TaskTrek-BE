package com.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import io.ktor.server.config.ApplicationConfig
import java.util.Date

class JwtConfig() {

    companion object {
        lateinit var config : ApplicationConfig
        fun init(config : ApplicationConfig) {
            this.config = config
        }
    }

    private val issuer = config.property("jwt.issuer").getString()
    private val audience = config.property("jwt.audience").getString()
    private val validityInMs = 365L * 1000L * 60L * 60L * 24L

    fun generateToken(username: String) : String {

        return JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("username" , username)
            .withExpiresAt(getExpiration())
            .sign(Algorithm.HMAC256(getSecret()))
    }

    fun getVerifier() : JWTVerifier {
        return JWT.require(Algorithm.HMAC256(getSecret()))
            .withIssuer(issuer)
            .withAudience(audience)
            .build()
    }
    private fun getSecret(): String {
        return config.property("jwt.secret").getString() ?: throw IllegalStateException("JWT_SECRET is not set")
    }


    fun decodeToken(token : String) : DecodedJWT {
        return getVerifier().verify(token.replace("Bearer ", ""))
    }

    private fun getExpiration() = Date(System.currentTimeMillis() + validityInMs)
}

//fun Application.configureJWTAuthentication(config: JWTConfig) {
//    install(Authentication) {
//        jwt("jwt-auth") {
//            realm = config.realm
//
//            val jwtVerifier = JWT
//                .require(Algorithm.HMAC256(config.secret))
//                .withAudience(config.audience)
//                .withIssuer(config.issuer)
//                .build()
//
//            verifier(jwtVerifier)
//
//            validate { jwtCredential ->
//                val username = jwtCredential.payload.getClaim("username").asString()
//                if (!username.isNullOrBlank()) {
//                    JWTPrincipal(jwtCredential.payload)
//                } else {
//                    null
//                }
//            }
//
//            challenge { _, _ ->
//                call.respondText("Token is not valid of has expired",
//                    status = HttpStatusCode.Unauthorized)
//            }
//        }
//    }
//}
//
//fun generateToken(config: JWTConfig, username: String): String {
//    return JWT.create()
//        .withAudience(config.audience)
//        .withIssuer(config.issuer)
//        .withClaim("username", username)
//        .withExpiresAt(Date(System.currentTimeMillis() + config.tokenExpiry))
//        .sign(Algorithm.HMAC256(config.secret))
//}