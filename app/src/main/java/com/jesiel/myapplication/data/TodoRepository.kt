package com.jesiel.myapplication.data

class TodoRepository {
    private val apiService = ApiService()

    // Fetches the entire record (Tasks + Habits)
    suspend fun getFullRecord(): TodoRecord {
        return apiService.getTodos().record
    }

    // Legacy method for compatibility with existing TodoViewModel
    suspend fun getTodos(): List<Task> {
        return getFullRecord().todos
    }

    // Updates the entire record on the server
    suspend fun updateFullRecord(record: TodoRecord) {
        apiService.updateTodos(record)
    }

    // Compatibility method: updates only todos while preserving existing habits
    suspend fun updateTodos(todos: List<Task>) {
        val currentRecord = getFullRecord()
        updateFullRecord(currentRecord.copy(todos = todos))
    }
}
