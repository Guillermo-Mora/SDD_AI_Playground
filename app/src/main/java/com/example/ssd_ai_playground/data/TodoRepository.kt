package com.example.ssd_ai_playground.data

import com.example.ssd_ai_playground.data.local.Todo
import com.example.ssd_ai_playground.data.local.TodoDao
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    fun observeAll(): Flow<List<Todo>>

    fun observeById(id: Long): Flow<Todo?>

    suspend fun insert(todo: Todo): Long

    suspend fun update(todo: Todo)

    suspend fun delete(todo: Todo)

    suspend fun setDone(id: Long, isDone: Boolean, updatedAt: Long)
}

class DefaultTodoRepository(private val dao: TodoDao) : TodoRepository {
    override fun observeAll(): Flow<List<Todo>> = dao.observeAll()

    override fun observeById(id: Long): Flow<Todo?> = dao.observeById(id)

    override suspend fun insert(todo: Todo): Long = dao.insert(todo)

    override suspend fun update(todo: Todo) = dao.update(todo)

    override suspend fun delete(todo: Todo) = dao.delete(todo)

    override suspend fun setDone(id: Long, isDone: Boolean, updatedAt: Long) =
        dao.setDone(id, isDone, updatedAt)
}
