package com.jesiel.myapplication.viewmodel

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jesiel.myapplication.data.PreferenceManager
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

// Represents the state of the UI
data class TodoUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val backgroundImageUrl: String = "",
    val blurIntensity: Float = 20f
)

// One-time events to be sent to the UI
sealed interface UiEvent {
    data class ShowUndoSnackbar(val task: Task) : UiEvent
}

class TodoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TodoRepository()
    private val preferenceManager = PreferenceManager(application)

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
            val lastUpdateDay = preferenceManager.lastImageUpdateDay.first()
            val today = LocalDate.now().toEpochDay()

            _uiState.update { it.copy(blurIntensity = savedBlur) }

            if (savedUrl.isEmpty() || lastUpdateDay != today) {
                refreshBackgroundImage()
            } else {
                _uiState.update { it.copy(backgroundImageUrl = savedUrl) }
            }
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

    fun updateBlurIntensity(intensity: Float) {
        _uiState.update { it.copy(blurIntensity = intensity) }
        viewModelScope.launch {
            preferenceManager.setBlurIntensity(intensity)
        }
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

    fun addTodo(context: Context, title: String, description: String?, category: String?, color: String?, reminder: Long?) {
        viewModelScope.launch {
            val currentTasks = _uiState.value.tasks
            val newId = (currentTasks.maxOfOrNull { it.id } ?: 0) + 1
            
            val formatter = DateTimeFormatter.ofPattern("MMM d, HH:mm", Locale("pt", "BR"))
            val currentTime = LocalDateTime.now().format(formatter).replaceFirstChar { it.uppercase() }
            
            val newTask = Task(
                id = newId, 
                title = title, 
                description = description,
                category = category,
                color = color,
                done = false,
                status = TaskStatus.PENDING,
                created = currentTime,
                reminder = reminder
            )

            _uiState.update { it.copy(tasks = currentTasks + newTask) }

            reminder?.let { time ->
                if (time > System.currentTimeMillis()) {
                    scheduleNotification(context, time, title, description, newId)
                }
            }

            try {
                repository.updateTodos(_uiState.value.tasks)
            } catch (e: Exception) {
                _uiState.update { it.copy(tasks = currentTasks, error = e.message) }
                e.printStackTrace()
            }
        }
    }

    fun updateTask(context: Context, taskId: Int, title: String, description: String?, category: String?, color: String?, reminder: Long?) {
        viewModelScope.launch {
            val currentTasks = _uiState.value.tasks
            val updatedTasks = currentTasks.map {
                if (it.id == taskId) {
                    it.copy(
                        title = title,
                        description = description,
                        category = category,
                        color = color,
                        reminder = reminder
                    )
                } else it
            }

            _uiState.update { it.copy(tasks = updatedTasks) }

            cancelNotification(context, taskId)
            reminder?.let { time ->
                if (time > System.currentTimeMillis()) {
                    scheduleNotification(context, time, title, description, taskId)
                }
            }

            try {
                repository.updateTodos(updatedTasks)
            } catch (e: Exception) {
                _uiState.update { it.copy(tasks = currentTasks, error = e.message) }
                e.printStackTrace()
            }
        }
    }

    fun updateTaskStatus(context: Context, taskId: Int, newStatus: TaskStatus) {
        viewModelScope.launch {
            val currentTasks = _uiState.value.tasks
            val updatedTasks = currentTasks.map {
                if (it.id == taskId) {
                    it.copy(
                        status = newStatus,
                        done = newStatus == TaskStatus.DONE
                    )
                } else it
            }

            _uiState.update { it.copy(tasks = updatedTasks) }

            if (newStatus == TaskStatus.DONE) {
                cancelNotification(context, taskId)
            }

            try {
                repository.updateTodos(updatedTasks)
            } catch (e: Exception) {
                _uiState.update { it.copy(tasks = currentTasks, error = e.message) }
                e.printStackTrace()
            }
        }
    }

    private fun scheduleNotification(context: Context, time: Long, title: String, description: String?, id: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                context.startActivity(intent)
                return
            }
        }

        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("task_title", title)
            putExtra("task_description", description)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
    }

    private fun cancelNotification(context: Context, id: Int) {
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    fun toggleTaskStatus(context: Context, taskId: Int) {
        viewModelScope.launch {
            val currentTasks = _uiState.value.tasks
            var isNowDone = false
            val updatedTasks = currentTasks.map {
                if (it.id == taskId) {
                    isNowDone = !it.done
                    it.copy(
                        done = isNowDone,
                        status = if (isNowDone) TaskStatus.DONE else TaskStatus.PENDING
                    )
                } else it
            }

            _uiState.update { it.copy(tasks = updatedTasks) }

            if (isNowDone) {
                cancelNotification(context, taskId)
            } else {
                val task = updatedTasks.find { it.id == taskId }
                task?.reminder?.let { time ->
                    if (time > System.currentTimeMillis()) {
                        scheduleNotification(context, time, task.title, task.description, taskId)
                    }
                }
            }

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
            val taskToDelete = _uiState.value.tasks.find { it.id == taskId } ?: return@launch
            lastDeletedTask = taskToDelete
            _uiState.update { it.copy(tasks = it.tasks.filterNot { t -> t.id == taskId }) }
            _eventChannel.send(UiEvent.ShowUndoSnackbar(taskToDelete))
        }
    }

    fun undoDelete(task: Task) {
        val currentTasks = _uiState.value.tasks
        _uiState.update { it.copy(tasks = (currentTasks + task).sortedBy { it.id }) }
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
                    e.printStackTrace()
                }
            }
        }
    }
}
