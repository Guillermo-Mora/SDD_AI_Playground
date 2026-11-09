package com.example.ssd_ai_playground.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM todos ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<Todo>>

    @Query("SELECT * FROM todos WHERE id = :id")
    fun observeById(id: Long): Flow<Todo?>

    @Insert
    suspend fun insert(todo: Todo): Long

    @Update
    suspend fun update(todo: Todo)

    @Delete
    suspend fun delete(todo: Todo)

    @Query("UPDATE todos SET isDone = :isDone, updatedAt = :updatedAt WHERE id = :id")
    suspend fun setDone(id: Long, isDone: Boolean, updatedAt: Long)
}
