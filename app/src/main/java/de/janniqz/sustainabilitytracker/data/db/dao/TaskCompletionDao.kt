package de.janniqz.sustainabilitytracker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import de.janniqz.sustainabilitytracker.data.model.entity.TaskCompletionEntity

@Dao
interface TaskCompletionDao {

    @Query("SELECT * FROM task_completions WHERE taskId = :taskId")
    suspend fun getAllByTask(taskId: Int): List<TaskCompletionEntity>

    @Query("SELECT * FROM task_completions WHERE taskId = :taskId AND completionTime <= :toDate AND completionTime >= :fromDate")
    suspend fun getAllByTaskBetweenDates(taskId: Int, fromDate: Long, toDate: Long): List<TaskCompletionEntity>

    @Query("SELECT COUNT(*) FROM task_completions WHERE taskId = :taskId")
    suspend fun getCountByTask(taskId: Int): Int

    @Insert
    suspend fun insert(taskCompletion: TaskCompletionEntity)

    @Delete
    suspend fun delete(taskCompletion: TaskCompletionEntity)
}
