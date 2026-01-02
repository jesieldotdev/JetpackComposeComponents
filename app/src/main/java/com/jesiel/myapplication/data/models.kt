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
    val todos: List<Task>
)

@Serializable
data class Task(
    val id: Int,
    val title: String,
    val description: String? = null,
    val category: String? = null,
    val color: String? = null,
    val done: Boolean = false, // Keep for backward compatibility if needed
    val status: TaskStatus = TaskStatus.PENDING, // New status field
    @SerialName("Created")
    val created: String? = null,
    val reminder: Long? = null
)
