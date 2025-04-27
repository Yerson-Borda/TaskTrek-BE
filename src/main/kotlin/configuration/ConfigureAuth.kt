package com.configuration

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.dto.token.TokenConfig
import com.utils.configureGoogleOAuth
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureAuth(config : TokenConfig, httpClient: HttpClient) {

    install(Authentication){
        jwt("auth_jwt") {
            verifier(
                JWT.require(Algorithm.HMAC256(config.secret))
                    .withIssuer(config.issuer)
                    .withAudience(config.audience)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(config.audience)) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }

        configureGoogleOAuth(httpClient)
    }
}

fun AuthenticationConfig.configureGoogleOAuth(httpClient: HttpClient) {
    oauth("google-oauth") {
        urlProvider = { "http://localhost:8080/callback" }
        providerLookup = {
            OAuthServerSettings.OAuth2ServerSettings(
                name = "google",
                authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
                accessTokenUrl = "https://www.googleapis.com/oauth2/v3/token",
                requestMethod = HttpMethod.Post,
                clientId = System.getenv("GOOGLE_CLIENT_ID") ?: "",
                clientSecret = System.getenv("GOOGLE_CLIENT_SECRET") ?: "",
                defaultScopes = listOf(
                    "https://www.googleapis.com/auth/userinfo.profile",
                    "https://www.googleapis.com/auth/userinfo.email"
                ),
                extraAuthParameters = listOf("access_type" to "offline")
            )
        }
        client = httpClient
    }
}