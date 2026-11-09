package com.example.ssd_ai_playground.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ssd_ai_playground.ui.detail.DetailScreen
import com.example.ssd_ai_playground.ui.form.FormScreen
import com.example.ssd_ai_playground.ui.list.ListScreen

@Composable
fun TodoNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = TodoList,
    ) {
        composable<TodoList> {
            ListScreen(
                onOpenTodo = { todoId -> navController.navigate(TodoDetail(todoId)) },
                onCreateTodo = { navController.navigate(TodoCreate) },
            )
        }
        composable<TodoDetail> {
            DetailScreen(
                onBack = { navController.popBackStack() },
                onEdit = { todoId -> navController.navigate(TodoEdit(todoId)) },
                onDeleted = { navController.popBackStack() },
            )
        }
        composable<TodoCreate> {
            FormScreen(
                onSaved = { navController.popBackStack() },
                onBack = { navController.popBackStack() },
            )
        }
        composable<TodoEdit> {
            FormScreen(
                onSaved = { navController.popBackStack() },
                onBack = { navController.popBackStack() },
            )
        }
    }
}
