package com.jesiel.myapplication.data

class TodoRepository {
    private val apiService = ApiService()

    suspend fun getTodos(): List<Task> {
        return apiService.getTodos().record.todos
    }
}