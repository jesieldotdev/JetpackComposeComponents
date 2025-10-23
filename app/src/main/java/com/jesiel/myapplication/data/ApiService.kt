package com.jesiel.myapplication.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class ApiService {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        defaultRequest {
            header(
                "X-Access-Key",
                // Escaping special characters to avoid syntax errors
                "\$2a\$10\$QlRV59fGXWXCo/ZwlxGtJOqlnZGcC8AMB0CtvDkuYA5RaQ.2bDgdS"
            )
        }
    }

    private val todoBinUrl = "https://api.jsonbin.io/v3/b/68f87724d0ea881f40b282c3"

    suspend fun getTodos(): ApiResponse {
        return client.get("$todoBinUrl/latest").body()
    }

    // This function handles the PUT request to update the data on the server
    suspend fun updateTodos(todos: TodoRecord) {
        client.put(todoBinUrl) {
            contentType(ContentType.Application.Json)
            setBody(todos)
        }
    }
}