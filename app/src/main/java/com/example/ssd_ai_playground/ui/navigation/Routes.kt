package com.example.ssd_ai_playground.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object TodoList : NavKey

@Serializable
data class TodoDetail(val todoId: Long) : NavKey

@Serializable
data object TodoCreate : NavKey

@Serializable
data class TodoEdit(val todoId: Long) : NavKey
