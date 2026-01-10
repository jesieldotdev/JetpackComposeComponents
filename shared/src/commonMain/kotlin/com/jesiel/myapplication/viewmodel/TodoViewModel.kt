package com.jesiel.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jesiel.myapplication.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class TodoUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val backgroundImageUrl: String = "",
    val blurIntensity: Float = 20f,
    val showBackgroundImage: Boolean = true,
    val selectedCategory: String = "Tudo"
)

sealed interface UiEvent {
    data class ShowUndoSnackbar(val task: Task) : UiEvent
    data class NavigateToTask(val taskId: Int) : UiEvent
}

class TodoViewModel(
    private val preferenceManager: IPreferenceManager,
    private val reminderManager: IReminderManager,
    private val apiService: ApiService = ApiService()
) : ViewModel() {

    private val _uiState = MutableStateFlow(TodoUiState())
    val uiState: StateFlow<TodoUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<UiEvent>()
    val eventFlow = _eventChannel.receiveAsFlow()

    private var lastDeletedTask: Task? = null

    init {
        loadSettings()
        fetchTodos()
    }

    fun fetchTodos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = apiService.getTodos()
                _uiState.update { it.copy(tasks = response.record.todos, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            combine(
                preferenceManager.blurIntensity,
                preferenceManager.showBackgroundImage,
                preferenceManager.backgroundImageUrl,
                preferenceManager.selectedTaskCategory
            ) { blur, showBg, url, category ->
                _uiState.update { it.copy(
                    blurIntensity = blur,
                    showBackgroundImage = showBg,
                    backgroundImageUrl = if (url.isNotEmpty()) url else "https://picsum.photos/1000/1800",
                    selectedCategory = category
                ) }
            }.collect()
        }
    }

    fun toggleTaskStatus(taskId: Int) {
        viewModelScope.launch {
            val currentTasks = _uiState.value.tasks
            val updatedTasks = currentTasks.map {
                if (it.id == taskId) {
                    val isNowDone = !it.done
                    it.copy(done = isNowDone, status = if (isNowDone) TaskStatus.DONE else TaskStatus.PENDING)
                } else it
            }
            _uiState.update { it.copy(tasks = updatedTasks) }
            saveTasks(updatedTasks)
        }
    }

    fun addTodo(title: String, description: String?, category: String?, color: String?, reminder: Long?) {
        viewModelScope.launch {
            val currentTasks = _uiState.value.tasks
            val newId = (currentTasks.maxOfOrNull { it.id } ?: 0) + 1
            val newTask = Task(
                id = newId, 
                title = title, 
                description = description, 
                category = category, 
                color = color, 
                done = false, 
                status = TaskStatus.PENDING,
                reminder = reminder
            )
            val updatedTasks = currentTasks + newTask
            _uiState.update { it.copy(tasks = updatedTasks) }
            saveTasks(updatedTasks)
            
            reminder?.let { 
                reminderManager.scheduleReminder(it, title, description, newId) 
            }
        }
    }

    fun updateTask(taskId: Int, title: String, description: String?, category: String?, color: String?, reminder: Long?) {
        viewModelScope.launch {
            val currentTasks = _uiState.value.tasks
            val updatedTasks = currentTasks.map {
                if (it.id == taskId) {
                    it.copy(title = title, description = description, category = category, color = color, reminder = reminder)
                } else it
            }
            _uiState.update { it.copy(tasks = updatedTasks) }
            saveTasks(updatedTasks)

            reminderManager.cancelReminder(taskId)
            reminder?.let { reminderManager.scheduleReminder(it, title, description, taskId) }
        }
    }

    fun deleteTodo(taskId: Int) {
        viewModelScope.launch {
            val taskToDelete = _uiState.value.tasks.find { it.id == taskId } ?: return@launch
            lastDeletedTask = taskToDelete
            val updatedTasks = _uiState.value.tasks.filterNot { it.id == taskId }
            _uiState.update { it.copy(tasks = updatedTasks) }
            saveTasks(updatedTasks)
            reminderManager.cancelReminder(taskId)
            _eventChannel.send(UiEvent.ShowUndoSnackbar(taskToDelete))
        }
    }

    fun undoDelete(task: Task) {
        viewModelScope.launch {
            val updatedTasks = (_uiState.value.tasks + task).sortedBy { it.id }
            _uiState.update { it.copy(tasks = updatedTasks) }
            saveTasks(updatedTasks)
        }
    }

    private suspend fun saveTasks(tasks: List<Task>) {
        try {
            apiService.updateTodos(TodoRecord(todos = tasks))
        } catch (e: Exception) {
            _uiState.update { it.copy(error = "Erro ao salvar: ${e.message}") }
        }
    }

    fun refreshBackgroundImage() {
        viewModelScope.launch {
            val url = "https://picsum.photos/1000/1800?random=${System.currentTimeMillis()}"
            preferenceManager.setBackgroundImage(url, 0L)
        }
    }

    fun updateBackgroundImage(url: String) {
        viewModelScope.launch {
            preferenceManager.setBackgroundImage(url, 0L)
        }
    }

    fun setShowBackgroundImage(enabled: Boolean) {
        viewModelScope.launch {
            preferenceManager.setShowBackgroundImage(enabled)
        }
    }

    fun updateBlurIntensity(intensity: Float) {
        viewModelScope.launch {
            preferenceManager.setBlurIntensity(intensity)
        }
    }

    fun setSelectedCategory(category: String) {
        viewModelScope.launch {
            preferenceManager.setSelectedTaskCategory(category)
        }
    }

    fun confirmDeletion() {
        lastDeletedTask = null
    }

    fun refresh() = fetchTodos()
}
