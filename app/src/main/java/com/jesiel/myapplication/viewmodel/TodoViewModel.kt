package com.jesiel.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jesiel.myapplication.data.Task
import com.jesiel.myapplication.data.TodoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Represents the state of the UI
data class TodoUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class TodoViewModel : ViewModel() {

    private val repository = TodoRepository()

    private val _uiState = MutableStateFlow(TodoUiState())
    val uiState: StateFlow<TodoUiState> = _uiState.asStateFlow()

    init {
        fetchTodos()
    }

    fun refresh() {
        fetchTodos()
    }

    private fun fetchTodos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val tasks = repository.getTodos()
                _uiState.update { it.copy(tasks = tasks, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
                e.printStackTrace()
            }
        }
    }

    fun addTodo(title: String) {
        viewModelScope.launch {
            val currentTasks = _uiState.value.tasks
            val newId = (currentTasks.maxOfOrNull { it.id } ?: 0) + 1
            val newTask = Task(id = newId, title = title, done = false)
            
            _uiState.update { it.copy(tasks = currentTasks + newTask) }

            try {
                repository.updateTodos(_uiState.value.tasks)
            } catch (e: Exception) {
                _uiState.update { it.copy(tasks = currentTasks, error = e.message) }
                e.printStackTrace()
            }
        }
    }

    fun toggleTaskStatus(taskId: Int) {
        viewModelScope.launch {
            val currentTasks = _uiState.value.tasks
            val updatedTasks = currentTasks.map {
                if (it.id == taskId) it.copy(done = !it.done) else it
            }

            _uiState.update { it.copy(tasks = updatedTasks) }

            try {
                repository.updateTodos(updatedTasks)
            } catch (e: Exception) {
                _uiState.update { it.copy(tasks = currentTasks, error = e.message) }
                e.printStackTrace()
            }
        }
    }

    fun deleteTodo(taskId: Int) {
        viewModelScope.launch {
            val currentTasks = _uiState.value.tasks
            val updatedTasks = currentTasks.filter { it.id != taskId }

            // Optimistic update
            _uiState.update { it.copy(tasks = updatedTasks) }

            try {
                repository.updateTodos(updatedTasks)
            } catch (e: Exception) {
                // Revert on error
                _uiState.update { it.copy(tasks = currentTasks, error = e.message) }
                e.printStackTrace()
            }
        }
    }
}