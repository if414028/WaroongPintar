package com.nesher.waroongpintar.network

import com.nesher.waroongpintar.BuildConfig
import com.nesher.waroongpintar.utils.UserConfiguration
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object ApiClient {
    fun create(userConfiguration: UserConfiguration): HttpClient =
        HttpClient(Android) {
            expectSuccess = true

            defaultRequest {
                url(ApiConfig.baseUrl)
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                header("X-Requested-With", "XMLHttpRequest")
                userConfiguration.accessToken?.let { bearerAuth(it) }
            }

            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                        explicitNulls = false
                    }
                )
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 30_000
                connectTimeoutMillis = 15_000
                socketTimeoutMillis = 30_000
            }

            install(HttpRequestRetry) {
                retryOnServerErrors(maxRetries = 1)
                exponentialDelay()
            }

            if (BuildConfig.DEBUG) {
                install(Logging) {
                    level = LogLevel.INFO
                }
            }
        }
}
