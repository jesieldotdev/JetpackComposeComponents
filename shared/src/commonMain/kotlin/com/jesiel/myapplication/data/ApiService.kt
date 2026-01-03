package com.jesiel.myapplication.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
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

// Criamos uma função para fornecer o cliente Ktor em cada plataforma
// No Desktop ele usará o motor CIO
expect fun getHttpClient(): HttpClient

class ApiService {
    private val client = getHttpClient()

    private val todoBinUrl = "https://api.jsonbin.io/v3/b/68f87724d0ea881f40b282c3"

    suspend fun getTodos(): ApiResponse {
        return client.get("$todoBinUrl/latest").body()
    }

    suspend fun updateTodos(record: TodoRecord) {
        client.put(todoBinUrl) {
            contentType(ContentType.Application.Json)
            setBody(record)
        }
    }
}
