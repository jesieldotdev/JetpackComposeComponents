package com.jesiel.myapplication.data

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
    val done: Boolean
)
