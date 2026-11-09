package com.example.ssd_ai_playground.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
data object TodoList

@Serializable
data class TodoDetail(val todoId: Long)

@Serializable
data object TodoCreate

@Serializable
data class TodoEdit(val todoId: Long)
