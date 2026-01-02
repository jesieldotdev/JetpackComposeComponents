package com.jesiel.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jesiel.myapplication.data.Habit
import com.jesiel.myapplication.data.HabitPeriod
import com.jesiel.myapplication.data.TodoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Calendar

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
                val today = LocalDate.now()
                val todayEpoch = today.toEpochDay()

                val processedHabits = habits.map { habit ->
                    val lastUpdate = habit.lastUpdatedDay?.let { LocalDate.ofEpochDay(it) }
                    
                    val shouldReset = when (habit.period) {
                        HabitPeriod.DAILY -> todayEpoch != habit.lastUpdatedDay
                        HabitPeriod.WEEKLY -> lastUpdate?.let { ChronoUnit.WEEKS.between(it, today) >= 1 } ?: true
                        HabitPeriod.MONTHLY -> lastUpdate?.let { ChronoUnit.MONTHS.between(it, today) >= 1 } ?: true
                    }

                    if (shouldReset) {
                        val yesterdayEpoch = todayEpoch - 1
                        val newStreak = if (habit.lastCompletedDay == yesterdayEpoch || habit.period != HabitPeriod.DAILY) habit.streak else 0
                        habit.copy(currentProgress = 0, lastUpdatedDay = todayEpoch, streak = newStreak)
                    } else habit
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
            val todayEpoch = LocalDate.now().toEpochDay()
            
            val updatedHabits = currentHabits.map { habit ->
                if (habit.id == habitId) {
                    val nextProgress = habit.currentProgress + 1
                    if (nextProgress <= habit.goal) {
                        val isNowCompleted = nextProgress == habit.goal
                        habit.copy(
                            currentProgress = nextProgress,
                            lastUpdatedDay = todayEpoch,
                            streak = if (isNowCompleted) habit.streak + 1 else habit.streak,
                            lastCompletedDay = if (isNowCompleted) todayEpoch else habit.lastCompletedDay
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
                lastUpdatedDay = LocalDate.now().toEpochDay(),
                streak = 0,
                streakGoal = streakGoal
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

    fun updateHabit(habitId: Int, title: String, goal: Int, unit: String, color: String?, streak: Int, streakGoal: Int, period: HabitPeriod) {
        viewModelScope.launch {
            val currentHabits = _uiState.value.habits
            val updatedHabits = currentHabits.map {
                if (it.id == habitId) {
                    it.copy(
                        title = title,
                        goal = goal,
                        unit = unit,
                        color = color,
                        streak = streak,
                        streakGoal = streakGoal,
                        period = period
                    )
                } else it
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
