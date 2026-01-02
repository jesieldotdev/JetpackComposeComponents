package com.jesiel.myapplication.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class TaskStatus {
    PENDING, IN_PROGRESS, DONE
}

@Serializable
data class ApiResponse(
    val record: TodoRecord
)

@Serializable
data class TodoRecord(
    val todos: List<Task>,
    val habits: List<Habit> = emptyList()
)

@Serializable
data class Task(
    val id: Int,
    val title: String,
    val description: String? = null,
    val category: String? = null,
    val color: String? = null,
    val done: Boolean = false,
    val status: TaskStatus = TaskStatus.PENDING,
    @SerialName("Created")
    val created: String? = null,
    val reminder: Long? = null
)

@Serializable
data class Habit(
    val id: Int,
    val title: String,
    val goal: Int,
    val currentProgress: Int = 0,
    val unit: String,
    val color: String? = null,
    val lastUpdatedDay: Long? = null,
    val streak: Int = 0, // Current consecutive days completed
    val lastCompletedDay: Long? = null // To track the streak logic
)
