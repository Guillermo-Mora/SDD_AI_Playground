package com.example.ssd_ai_playground.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.ssd_ai_playground.TodoApplication
import com.example.ssd_ai_playground.ui.detail.DetailViewModel
import com.example.ssd_ai_playground.ui.form.FormViewModel
import com.example.ssd_ai_playground.ui.list.ListViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            ListViewModel(todoApplication().container.todoRepository)
        }
        initializer {
            DetailViewModel(
                savedStateHandle = createSavedStateHandle(),
                repository = todoApplication().container.todoRepository,
            )
        }
        initializer {
            FormViewModel(
                savedStateHandle = createSavedStateHandle(),
                repository = todoApplication().container.todoRepository,
            )
        }
    }
}

private fun CreationExtras.todoApplication(): TodoApplication =
    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TodoApplication
