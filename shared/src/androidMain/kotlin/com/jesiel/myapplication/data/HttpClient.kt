package com.jesiel.myapplication.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

actual fun getHttpClient(): HttpClient = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            isLenient = true
        })
    }
    defaultRequest {
        header("X-Access-Key", "\$2a\$10\$QlRV59fGXWXCo/ZwlxGtJOqlnZGcC8AMB0CtvDkuYA5RaQ.2bDgdS")
    }
}
