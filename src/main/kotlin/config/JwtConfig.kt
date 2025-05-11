package com.config

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.model.token.Token
import java.util.Date
import java.util.UUID

class JwtConfig(private val token: Token) {

    fun generateToken(userId: UUID): String {
        return JWT.create()
            .withIssuer(token.issuer)
            .withAudience(token.audience)
            .withClaim("userId", userId.toString())
            .withExpiresAt(Date(System.currentTimeMillis() + token.tokenExpiry))
            .sign(Algorithm.HMAC256(token.secret))
    }

    fun getVerifier(): JWTVerifier {
        return JWT.require(Algorithm.HMAC256(token.secret))
            .withIssuer(token.issuer)
            .withAudience(token.audience)
            .build()
    }
}
