package de.janniqz.sustainabilitytracker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import de.janniqz.sustainabilitytracker.data.model.TaskCategory
import de.janniqz.sustainabilitytracker.data.model.entity.TaskEntity

@Dao
interface TaskDao {

    /**
     * Retrieves all TaskEntities
     */
    @Query("SELECT * FROM tasks")
    suspend fun getAll(): List<TaskEntity>

    /**
     * Retrieves all TaskEntities with the specified Category
     * @see TaskCategory
     */
    @Query("SELECT * FROM tasks WHERE category = :category")
    suspend fun getAllByCategory(category: TaskCategory): List<TaskEntity>

    /**
     * Inserts a new TaskEntity into the Database
     */
    @Insert
    suspend fun insert(task: TaskEntity)

    /**
     * Updates an existing TaskEntity in the Database
     */
    @Update
    suspend fun update(task: TaskEntity)

    /**
     * Deletes an existing TaskEntity from the Database
     */
    @Delete
    suspend fun delete(task: TaskEntity)
}
