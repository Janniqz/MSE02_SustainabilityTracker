package de.janniqz.sustainabilitytracker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import de.janniqz.sustainabilitytracker.data.model.entity.GoalEntity

/**
 * Data Access Object for GoalEntities
 * @see GoalEntity
 */
@Dao
interface GoalDao {

    /**
     * Retrieves all GoalEntities ordered by Category + Periodicity
     */
    @Query("SELECT * FROM goals ORDER BY category, periodicity")
    suspend fun getAll(): List<GoalEntity>

    /**
     * Inserts a new GoalEntity into the Database
     */
    @Insert
    suspend fun insert(goal: GoalEntity)

    /**
     * Updates an existing GoalEntity in the Database
     */
    @Update
    suspend fun update(goal: GoalEntity)

    /**
     * Deletes an existing GoalEntity from the Database
     */
    @Delete
    suspend fun delete(goal: GoalEntity)
}
