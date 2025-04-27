package com.utils

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.auth.AuthenticationConfig
import io.ktor.server.auth.OAuthServerSettings
import io.ktor.server.auth.oauth
import kotlinx.serialization.Serializable

fun AuthenticationConfig.configureGoogleOAuth(httpClient: HttpClient){
    oauth("google-oauth"){
        urlProvider = { "http://localhost:8080/callback" }
        providerLookup = {
            OAuthServerSettings.OAuth2ServerSettings(
                name = "google",
                authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
                accessTokenUrl = "https://www.googleapis.com/oauth2/v3/token",
                requestMethod = HttpMethod.Post,
                clientId = System.getenv("GOOGLE_CLIENT_ID"),
                clientSecret = System.getenv("GOOGLE_CLIENT_SECRET"),
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

@Serializable
data class UserInfo(
    val name: String,
    val email: String
)

@Serializable
data class GoogleTokenRequest(val token: String)

@Serializable
data class GoogleUserResponse(
    val name: String,
    val email: String,
    val picture: String? = null
)

suspend fun fetchGoogleUserInfo(httpClient: HttpClient, accessToken: String): GoogleUserResponse? {
    return try {
        val response: HttpResponse = httpClient.get("https://www.googleapis.com/oauth2/v2/userinfo") {
            header(HttpHeaders.Authorization, "Bearer $accessToken")
        }

        // Add debug logging:
        println("Google API response status: ${response.status}")
        println("Response body: ${response.bodyAsText()}")

        response.body()
    } catch (e: Exception) {
        println("Google API call failed: ${e.message}")
        null
    }
}





























