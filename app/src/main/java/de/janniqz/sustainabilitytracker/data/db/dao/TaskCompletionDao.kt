package de.janniqz.sustainabilitytracker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import de.janniqz.sustainabilitytracker.data.model.entity.TaskCompletionEntity

/**
 * Data Access Object for TaskCompletionEntities
 * @see TaskCompletionEntity
 */
@Dao
interface TaskCompletionDao {

    /**
     * Retrieves Task Completions for the specified taskId in the given Time Range
     * @param taskId Database ID of a TaskEntity
     * @param fromDate Time Range Start Date
     * @param toDate Time Range End Date
     * @see de.janniqz.sustainabilitytracker.data.model.entity.TaskEntity
     */
    @Query("SELECT * FROM task_completions WHERE taskId = :taskId AND completionTime <= :toDate AND completionTime >= :fromDate")
    suspend fun getAllByTaskBetweenDates(taskId: Int, fromDate: Long, toDate: Long): List<TaskCompletionEntity>

    /**
     * Retrieves the total number of Completions for the specified taskId
     * @param taskId Database ID of a TaskEntity
     * @see de.janniqz.sustainabilitytracker.data.model.entity.TaskEntity
     */
    @Query("SELECT COUNT(*) FROM task_completions WHERE taskId = :taskId")
    suspend fun getCountByTask(taskId: Int): Int

    /**
     * Retrieves the number of Completions for the specified taskId in the given Time Range
     * @param taskId Database ID of a TaskEntity
     * @param fromDate Time Range Start Date
     * @param toDate Time Range End Date
     * @see de.janniqz.sustainabilitytracker.data.model.entity.TaskEntity
     */
    @Query("SELECT COUNT(*) FROM task_completions WHERE taskId = :taskId AND completionTime <= :toDate AND completionTime >= :fromDate")
    suspend fun getCountByTaskBetweenDates(taskId: Int, fromDate: Long, toDate: Long): Int

    /**
     * Inserts a new TaskCompletionEntity into the Database
     */
    @Insert
    suspend fun insert(taskCompletion: TaskCompletionEntity)
}
