package com.example.ssd_ai_playground.ui.form

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ssd_ai_playground.data.TodoRepository
import com.example.ssd_ai_playground.data.local.Todo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FormUiState(
    val isEdit: Boolean = false,
    val isLoading: Boolean = false,
    val title: String = "",
    val notes: String = "",
    val initialTitle: String = "",
    val initialNotes: String = "",
    val isSaving: Boolean = false,
    val notFound: Boolean = false,
) {
    val isDirty: Boolean
        get() = title != initialTitle || notes != initialNotes

    val canSave: Boolean
        get() = title.trim().isNotEmpty() && !isSaving && !isLoading && !notFound
}

class FormViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val repository: TodoRepository,
) : ViewModel() {

    private val todoId: Long? = savedStateHandle.get<Long>("todoId")
    private val isEdit = todoId != null

    private val _uiState = MutableStateFlow(
        FormUiState(
            isEdit = isEdit,
            title = savedStateHandle["title"] ?: "",
            notes = savedStateHandle["notes"] ?: "",
            initialTitle = savedStateHandle["initialTitle"] ?: "",
            initialNotes = savedStateHandle["initialNotes"] ?: "",
            isLoading = isEdit && savedStateHandle.get<Boolean>("loaded") != true,
        ),
    )
    val uiState: StateFlow<FormUiState> = _uiState.asStateFlow()

    init {
        if (todoId != null && savedStateHandle.get<Boolean>("loaded") != true) {
            viewModelScope.launch {
                val todo = repository.observeById(todoId).first()
                if (todo == null) {
                    _uiState.update { it.copy(isLoading = false, notFound = true) }
                } else {
                    val title = savedStateHandle["title"] ?: todo.title
                    val notes = savedStateHandle["notes"] ?: todo.notes
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            title = title,
                            notes = notes,
                            initialTitle = todo.title,
                            initialNotes = todo.notes,
                        )
                    }
                    savedStateHandle["title"] = title
                    savedStateHandle["notes"] = notes
                    savedStateHandle["initialTitle"] = todo.title
                    savedStateHandle["initialNotes"] = todo.notes
                    savedStateHandle["loaded"] = true
                }
            }
        }
    }

    fun onTitleChange(value: String) {
        savedStateHandle["title"] = value
        _uiState.update { it.copy(title = value) }
    }

    fun onNotesChange(value: String) {
        savedStateHandle["notes"] = value
        _uiState.update { it.copy(notes = value) }
    }

    fun save(onSaved: () -> Unit) {
        val state = _uiState.value
        if (!state.canSave) return

        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val title = state.title.trim()
            if (todoId == null) {
                repository.insert(
                    Todo(
                        title = title,
                        notes = state.notes,
                        createdAt = now,
                        updatedAt = now,
                    ),
                )
            } else {
                val existing = repository.observeById(todoId).first()
                if (existing != null) {
                    repository.update(
                        existing.copy(
                            title = title,
                            notes = state.notes,
                            updatedAt = now,
                        ),
                    )
                }
            }
            _uiState.update { it.copy(isSaving = false) }
            onSaved()
        }
    }
}
