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

    init {
        loadSettings()
        fetchTodos()
    }

    private fun fetchTodos() {
        viewModelScope.launch(Dispatchers.Default) {
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
        viewModelScope.launch(Dispatchers.Default) {
            preferenceManager.blurIntensity.collect { intensity ->
                _uiState.update { it.copy(blurIntensity = intensity) }
            }
        }
        viewModelScope.launch(Dispatchers.Default) {
            preferenceManager.showBackgroundImage.collect { show ->
                _uiState.update { it.copy(showBackgroundImage = show) }
            }
        }
        viewModelScope.launch(Dispatchers.Default) {
            preferenceManager.backgroundImageUrl.collect { url ->
                if (url.isNotEmpty()) {
                    _uiState.update { it.copy(backgroundImageUrl = url) }
                } else {
                    _uiState.update { it.copy(backgroundImageUrl = "https://picsum.photos/1000/1800") }
                }
            }
        }
    }

    fun setShowBackgroundImage(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.Default) {
            preferenceManager.setShowBackgroundImage(enabled)
        }
    }

    fun updateBlurIntensity(intensity: Float) {
        viewModelScope.launch(Dispatchers.Default) {
            preferenceManager.setBlurIntensity(intensity)
        }
    }

    fun toggleTaskStatus(taskId: Int) {
        viewModelScope.launch(Dispatchers.Default) {
            val updatedTasks = _uiState.value.tasks.map {
                if (it.id == taskId) {
                    val isNowDone = !it.done
                    it.copy(done = isNowDone, status = if (isNowDone) TaskStatus.DONE else TaskStatus.PENDING)
                } else it
            }
            _uiState.update { it.copy(tasks = updatedTasks) }
        }
    }
}
