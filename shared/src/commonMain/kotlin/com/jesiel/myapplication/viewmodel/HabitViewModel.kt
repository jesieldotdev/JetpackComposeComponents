package com.jesiel.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jesiel.myapplication.data.Habit
import com.jesiel.myapplication.data.HabitPeriod
import com.jesiel.myapplication.data.ApiService
import com.jesiel.myapplication.data.TodoRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class HabitUiState(
    val habits: List<Habit> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class HabitViewModel(private val apiService: ApiService = ApiService()) : ViewModel() {

    private val _uiState = MutableStateFlow(HabitUiState())
    val uiState: StateFlow<HabitUiState> = _uiState.asStateFlow()

    init {
        fetchHabits()
    }

    fun fetchHabits() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = apiService.getTodos()
                val habits = response.record.habits
                _uiState.update { it.copy(habits = habits, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun incrementHabit(habitId: Int) {
        viewModelScope.launch {
            val updatedHabits = _uiState.value.habits.map { habit ->
                if (habit.id == habitId) {
                    val nextProgress = habit.currentProgress + 1
                    habit.copy(currentProgress = if (nextProgress <= habit.goal) nextProgress else habit.goal)
                } else habit
            }
            _uiState.update { it.copy(habits = updatedHabits) }
            saveHabits(updatedHabits)
        }
    }

    fun startNewOffensive(habitId: Int) {
        viewModelScope.launch {
            val updatedHabits = _uiState.value.habits.map { habit ->
                if (habit.id == habitId) {
                    habit.copy(
                        streak = 0,
                        completedOffensives = habit.completedOffensives + 1,
                        pastOffensives = habit.pastOffensives + habit.streak,
                        lastCompletedDay = null
                    )
                } else habit
            }
            _uiState.update { it.copy(habits = updatedHabits) }
            saveHabits(updatedHabits)
        }
    }

    fun updateHabit(habitId: Int, title: String, goal: Int, unit: String, color: String?, streak: Int, streakGoal: Int, period: HabitPeriod) {
        viewModelScope.launch {
            val updatedHabits = _uiState.value.habits.map { habit ->
                if (habit.id == habitId) {
                    habit.copy(
                        title = title,
                        goal = goal,
                        unit = unit,
                        color = color,
                        streak = streak,
                        streakGoal = streakGoal,
                        period = period
                    )
                } else habit
            }
            _uiState.update { it.copy(habits = updatedHabits) }
            saveHabits(updatedHabits)
        }
    }

    private suspend fun saveHabits(habits: List<Habit>) {
        try {
            val response = apiService.getTodos()
            val updatedRecord = response.record.copy(habits = habits)
            apiService.updateTodos(updatedRecord)
        } catch (e: Exception) {
            _uiState.update { it.copy(error = e.message) }
        }
    }

    fun addHabit(title: String, goal: Int, unit: String, color: String?, streakGoal: Int, period: HabitPeriod) {
        viewModelScope.launch {
            val currentHabits = _uiState.value.habits
            val newId = (currentHabits.maxOfOrNull { it.id } ?: 0) + 1
            val newHabit = Habit(
                id = newId,
                title = title,
                goal = goal,
                unit = unit,
                color = color,
                period = period,
                streak = 0,
                streakGoal = streakGoal
            )
            val updatedHabits = currentHabits + newHabit
            _uiState.update { it.copy(habits = updatedHabits) }
            saveHabits(updatedHabits)
        }
    }

    fun deleteHabit(habitId: Int) {
        viewModelScope.launch {
            val updatedHabits = _uiState.value.habits.filter { it.id != habitId }
            _uiState.update { it.copy(habits = updatedHabits) }
            saveHabits(updatedHabits)
        }
    }
}
