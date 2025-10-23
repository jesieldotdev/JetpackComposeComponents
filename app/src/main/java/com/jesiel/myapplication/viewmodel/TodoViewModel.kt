package com.jesiel.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jesiel.myapplication.data.Task
import com.jesiel.myapplication.data.TodoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TodoViewModel : ViewModel() {

    private val repository = TodoRepository()

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    init {
        fetchTodos()
    }

    private fun fetchTodos() {
        viewModelScope.launch {
            try {
                _tasks.value = repository.getTodos()
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        }
    }

    fun addTodo(title: String) {
        viewModelScope.launch {
            val currentTasks = _tasks.value
            val newId = (currentTasks.maxOfOrNull { it.id } ?: 0) + 1
            val newTask = Task(id = newId, title = title, done = false)
            
            // Optimistic update
            _tasks.value = currentTasks + newTask

            try {
                repository.updateTodos(_tasks.value)
            } catch (e: Exception) {
                // Revert on error
                _tasks.value = currentTasks
                e.printStackTrace()
            }
        }
    }
}