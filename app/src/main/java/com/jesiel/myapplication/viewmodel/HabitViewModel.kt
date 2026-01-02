package com.jesiel.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jesiel.myapplication.data.Habit
import com.jesiel.myapplication.data.TodoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class HabitUiState(
    val habits: List<Habit> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class HabitViewModel : ViewModel() {
    private val repository = TodoRepository()

    private val _uiState = MutableStateFlow(HabitUiState())
    val uiState: StateFlow<HabitUiState> = _uiState.asStateFlow()

    init {
        fetchHabits()
    }

    fun fetchHabits() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val record = repository.getFullRecord()
                val habits = record.habits
                val today = LocalDate.now().toEpochDay()
                val yesterday = today - 1

                val processedHabits = habits.map { habit ->
                    // Reset progress if it's a new day
                    val updatedHabit = if (habit.lastUpdatedDay != today) {
                        // Check if streak should be reset (if not completed yesterday)
                        val newStreak = if (habit.lastCompletedDay == yesterday) habit.streak else 0
                        habit.copy(currentProgress = 0, lastUpdatedDay = today, streak = newStreak)
                    } else habit
                    updatedHabit
                }

                if (processedHabits != habits) {
                    repository.updateFullRecord(record.copy(habits = processedHabits))
                }

                _uiState.update { it.copy(habits = processedHabits, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun incrementHabit(habitId: Int) {
        viewModelScope.launch {
            val currentHabits = _uiState.value.habits
            val today = LocalDate.now().toEpochDay()
            
            val updatedHabits = currentHabits.map { habit ->
                if (habit.id == habitId) {
                    val nextProgress = habit.currentProgress + 1
                    if (nextProgress <= habit.goal) {
                        val isNowCompleted = nextProgress == habit.goal
                        habit.copy(
                            currentProgress = nextProgress,
                            lastUpdatedDay = today,
                            streak = if (isNowCompleted) habit.streak + 1 else habit.streak,
                            lastCompletedDay = if (isNowCompleted) today else habit.lastCompletedDay
                        )
                    } else habit
                } else habit
            }

            _uiState.update { it.copy(habits = updatedHabits) }

            try {
                val currentRecord = repository.getFullRecord()
                repository.updateFullRecord(currentRecord.copy(habits = updatedHabits))
            } catch (e: Exception) {
                _uiState.update { it.copy(habits = currentHabits, error = e.message) }
            }
        }
    }

    fun addHabit(title: String, goal: Int, unit: String, color: String?) {
        viewModelScope.launch {
            val currentHabits = _uiState.value.habits
            val newId = (currentHabits.maxOfOrNull { it.id } ?: 0) + 1
            val newHabit = Habit(
                id = newId,
                title = title,
                goal = goal,
                unit = unit,
                color = color,
                lastUpdatedDay = LocalDate.now().toEpochDay(),
                streak = 0
            )

            val updatedHabits = currentHabits + newHabit
            _uiState.update { it.copy(habits = updatedHabits) }

            try {
                val currentRecord = repository.getFullRecord()
                repository.updateFullRecord(currentRecord.copy(habits = updatedHabits))
            } catch (e: Exception) {
                _uiState.update { it.copy(habits = currentHabits, error = e.message) }
            }
        }
    }

    fun deleteHabit(habitId: Int) {
        viewModelScope.launch {
            val currentHabits = _uiState.value.habits
            val updatedHabits = currentHabits.filter { it.id != habitId }
            _uiState.update { it.copy(habits = updatedHabits) }

            try {
                val currentRecord = repository.getFullRecord()
                repository.updateFullRecord(currentRecord.copy(habits = updatedHabits))
            } catch (e: Exception) {
                _uiState.update { it.copy(habits = currentHabits, error = e.message) }
            }
        }
    }
}
