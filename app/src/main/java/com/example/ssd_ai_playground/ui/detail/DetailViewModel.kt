package com.example.ssd_ai_playground.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ssd_ai_playground.data.TodoRepository
import com.example.ssd_ai_playground.data.local.Todo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface DetailUiState {
    data object Loading : DetailUiState

    data class Found(val todo: Todo) : DetailUiState

    data object NotFound : DetailUiState
}

class DetailViewModel(
    private val todoId: Long,
    private val repository: TodoRepository,
) : ViewModel() {

    val uiState: StateFlow<DetailUiState> = repository.observeById(todoId)
        .map { todo ->
            if (todo == null) DetailUiState.NotFound else DetailUiState.Found(todo)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DetailUiState.Loading,
        )

    fun toggleDone(todo: Todo) {
        viewModelScope.launch {
            repository.setDone(
                id = todo.id,
                isDone = !todo.isDone,
                updatedAt = System.currentTimeMillis(),
            )
        }
    }

    fun delete(todo: Todo, onDeleted: () -> Unit) {
        viewModelScope.launch {
            repository.delete(todo)
            onDeleted()
        }
    }
}
