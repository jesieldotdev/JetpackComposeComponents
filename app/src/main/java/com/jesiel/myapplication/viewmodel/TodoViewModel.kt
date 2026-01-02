package com.jesiel.myapplication.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jesiel.myapplication.data.PreferenceManager
import com.jesiel.myapplication.data.ReminderManager
import com.jesiel.myapplication.data.Task
import com.jesiel.myapplication.data.TaskStatus
import com.jesiel.myapplication.data.TodoRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

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

class TodoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TodoRepository()
    private val preferenceManager = PreferenceManager(application)
    private val reminderManager = ReminderManager(application)

    private val _uiState = MutableStateFlow(TodoUiState())
    val uiState: StateFlow<TodoUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<UiEvent>()
    val eventFlow = _eventChannel.receiveAsFlow()

    private var lastDeletedTask: Task? = null

    init {
        loadSettings()
        fetchTodos()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            val savedBlur = preferenceManager.blurIntensity.first()
            val savedUrl = preferenceManager.backgroundImageUrl.first()
            val savedShowBg = preferenceManager.showBackgroundImage.first()
            val savedCategory = preferenceManager.selectedTaskCategory.first()
            val lastUpdateDay = preferenceManager.lastImageUpdateDay.first()
            val today = LocalDate.now().toEpochDay()

            _uiState.update { it.copy(
                blurIntensity = savedBlur,
                showBackgroundImage = savedShowBg,
                selectedCategory = savedCategory
            ) }

            if (savedUrl.isEmpty() || lastUpdateDay != today) {
                refreshBackgroundImage()
            } else {
                _uiState.update { it.copy(backgroundImageUrl = savedUrl) }
            }
        }
    }

    fun setSelectedCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
        viewModelScope.launch {
            preferenceManager.setSelectedTaskCategory(category)
        }
    }

    fun refreshBackgroundImage() {
        viewModelScope.launch {
            val today = LocalDate.now().toEpochDay()
            val randomUrl = "https://picsum.photos/1000/1800?random=${System.currentTimeMillis()}"
            _uiState.update { it.copy(backgroundImageUrl = randomUrl) }
            preferenceManager.setBackgroundImage(randomUrl, today)
        }
    }

    fun setShowBackgroundImage(enabled: Boolean) {
        _uiState.update { it.copy(showBackgroundImage = enabled) }
        viewModelScope.launch {
            preferenceManager.setShowBackgroundImage(enabled)
        }
    }

    fun updateBlurIntensity(intensity: Float) {
        _uiState.update { it.copy(blurIntensity = intensity) }
        viewModelScope.launch { preferenceManager.setBlurIntensity(intensity) }
    }

    fun refresh() = fetchTodos()

    private fun fetchTodos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val tasks = repository.getTodos()
                _uiState.update { it.copy(tasks = tasks, isLoading = false) }
                syncNotifications(tasks)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    private fun syncNotifications(tasks: List<Task>) {
        tasks.forEach { task ->
            if (!task.done && task.reminder != null && task.reminder > System.currentTimeMillis()) {
                reminderManager.scheduleReminder(task.reminder, task.title, task.description, task.id)
            } else {
                reminderManager.cancelReminder(task.id)
            }
        }
    }

    fun addTodo(context: Context, title: String, description: String?, category: String?, color: String?, reminder: Long?) {
        viewModelScope.launch {
            val currentTasks = _uiState.value.tasks
            val newId = (currentTasks.maxOfOrNull { it.id } ?: 0) + 1
            val currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM d, HH:mm", Locale.forLanguageTag("pt-BR"))).replaceFirstChar { it.uppercase() }
            
            val newTask = Task(id = newId, title = title, description = description, category = category, color = color, done = false, status = TaskStatus.PENDING, created = currentTime, reminder = reminder)
            val updatedTasks = currentTasks + newTask
            _uiState.update { it.copy(tasks = updatedTasks) }

            reminder?.let { reminderManager.scheduleReminder(it, title, description, newId) }

            try {
                repository.updateTodos(updatedTasks)
            } catch (e: Exception) {
                _uiState.update { it.copy(tasks = currentTasks, error = e.message) }
            }
        }
    }

    fun updateTask(context: Context, taskId: Int, title: String, description: String?, category: String?, color: String?, reminder: Long?) {
        viewModelScope.launch {
            val currentTasks = _uiState.value.tasks
            val updatedTasks = currentTasks.map {
                if (it.id == taskId) it.copy(title = title, description = description, category = category, color = color, reminder = reminder) else it
            }
            _uiState.update { it.copy(tasks = updatedTasks) }

            reminderManager.cancelReminder(taskId)
            reminder?.let { reminderManager.scheduleReminder(it, title, description, taskId) }

            try {
                repository.updateTodos(updatedTasks)
            } catch (e: Exception) {
                _uiState.update { it.copy(tasks = currentTasks, error = e.message) }
            }
        }
    }

    fun updateTaskStatus(context: Context, taskId: Int, newStatus: TaskStatus) {
        viewModelScope.launch {
            val currentTasks = _uiState.value.tasks
            val updatedTasks = currentTasks.map {
                if (it.id == taskId) it.copy(status = newStatus, done = newStatus == TaskStatus.DONE) else it
            }
            _uiState.update { it.copy(tasks = updatedTasks) }

            if (newStatus == TaskStatus.DONE) {
                reminderManager.cancelReminder(taskId)
            } else {
                val task = updatedTasks.find { it.id == taskId }
                task?.reminder?.let { reminderManager.scheduleReminder(it, task.title, task.description, taskId) }
            }

            try {
                repository.updateTodos(updatedTasks)
            } catch (e: Exception) {
                _uiState.update { it.copy(tasks = currentTasks, error = e.message) }
            }
        }
    }

    fun toggleTaskStatus(context: Context, taskId: Int) {
        viewModelScope.launch {
            val currentTasks = _uiState.value.tasks
            var isNowDone = false
            val updatedTasks = currentTasks.map {
                if (it.id == taskId) {
                    isNowDone = !it.done
                    it.copy(done = isNowDone, status = if (isNowDone) TaskStatus.DONE else TaskStatus.PENDING)
                } else it
            }
            _uiState.update { it.copy(tasks = updatedTasks) }

            if (isNowDone) {
                reminderManager.cancelReminder(taskId)
            } else {
                val task = updatedTasks.find { it.id == taskId }
                task?.reminder?.let { reminderManager.scheduleReminder(it, task.title, task.description, taskId) }
            }

            try {
                repository.updateTodos(updatedTasks)
            } catch (e: Exception) {
                _uiState.update { it.copy(tasks = currentTasks, error = e.message) }
            }
        }
    }

    fun handleNotificationNavigation(taskId: Int) {
        viewModelScope.launch {
            _eventChannel.send(UiEvent.NavigateToTask(taskId))
        }
    }

    fun deleteTodo(taskId: Int) {
        viewModelScope.launch {
            val taskToDelete = _uiState.value.tasks.find { it.id == taskId } ?: return@launch
            lastDeletedTask = taskToDelete
            _uiState.update { it.copy(tasks = it.tasks.filterNot { t -> t.id == taskId }) }
            reminderManager.cancelReminder(taskId)
            _eventChannel.send(UiEvent.ShowUndoSnackbar(taskToDelete))
        }
    }

    fun undoDelete(task: Task) {
        val updatedTasks = (_uiState.value.tasks + task).sortedBy { it.id }
        _uiState.update { it.copy(tasks = updatedTasks) }
        task.reminder?.let { reminderManager.scheduleReminder(it, task.title, task.description, task.id) }
        lastDeletedTask = null
    }

    fun confirmDeletion() {
        viewModelScope.launch {
            if (lastDeletedTask != null) {
                try {
                    repository.updateTodos(_uiState.value.tasks)
                    lastDeletedTask = null
                } catch (e: Exception) {
                    fetchTodos()
                }
            }
        }
    }
}
