package com.example.ssd_ai_playground.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.ssd_ai_playground.ui.AppViewModelProvider
import com.example.ssd_ai_playground.ui.detail.DetailScreen
import com.example.ssd_ai_playground.ui.form.FormScreen
import com.example.ssd_ai_playground.ui.list.ListScreen

@Composable
fun TodoNavHost() {
    val backStack = rememberNavBackStack(TodoList)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<TodoList> {
                ListScreen(
                    onOpenTodo = { todoId -> backStack.add(TodoDetail(todoId)) },
                    onCreateTodo = { backStack.add(TodoCreate) },
                )
            }
            entry<TodoDetail> { key ->
                DetailScreen(
                    onBack = { backStack.removeLastOrNull() },
                    onEdit = { todoId -> backStack.add(TodoEdit(todoId)) },
                    onDeleted = { backStack.removeLastOrNull() },
                    viewModel = viewModel(
                        factory = AppViewModelProvider.detailFactory(key.todoId),
                    ),
                )
            }
            entry<TodoCreate> {
                FormScreen(
                    onSaved = { backStack.removeLastOrNull() },
                    onBack = { backStack.removeLastOrNull() },
                    viewModel = viewModel(
                        factory = AppViewModelProvider.formFactory(),
                    ),
                )
            }
            entry<TodoEdit> { key ->
                FormScreen(
                    onSaved = { backStack.removeLastOrNull() },
                    onBack = { backStack.removeLastOrNull() },
                    viewModel = viewModel(
                        factory = AppViewModelProvider.formFactory(key.todoId),
                    ),
                )
            }
        },
    )
}