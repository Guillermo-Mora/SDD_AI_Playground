package com.example.ssd_ai_playground.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todos")
data class Todo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val notes: String = "",
    val isDone: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long,
)
