package com.example.ssd_ai_playground.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ssd_ai_playground.data.TodoRepository
import com.example.ssd_ai_playground.data.local.Todo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ListUiState(
    val todos: List<Todo> = emptyList(),
    val isLoading: Boolean = true,
)

class ListViewModel(
    private val repository: TodoRepository,
) : ViewModel() {

    val uiState: StateFlow<ListUiState> = repository.observeAll()
        .map { todos -> ListUiState(todos = todos, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ListUiState(),
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

    fun delete(todo: Todo) {
        viewModelScope.launch {
            repository.delete(todo)
        }
    }
}
