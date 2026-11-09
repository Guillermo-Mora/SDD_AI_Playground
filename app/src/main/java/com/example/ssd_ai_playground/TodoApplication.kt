package com.example.ssd_ai_playground

import android.app.Application
import android.content.Context
import com.example.ssd_ai_playground.data.DefaultTodoRepository
import com.example.ssd_ai_playground.data.TodoRepository
import com.example.ssd_ai_playground.data.local.TodoDatabase

class AppContainer(context: Context) {
    private val database: TodoDatabase by lazy { TodoDatabase.getInstance(context) }

    val todoRepository: TodoRepository by lazy {
        DefaultTodoRepository(database.todoDao())
    }
}

class TodoApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
