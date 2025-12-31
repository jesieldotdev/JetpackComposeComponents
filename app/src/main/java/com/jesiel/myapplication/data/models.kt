package com.jesiel.myapplication.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
    val done: Boolean,
    @SerialName("Created")
    val created: String? = null
)
