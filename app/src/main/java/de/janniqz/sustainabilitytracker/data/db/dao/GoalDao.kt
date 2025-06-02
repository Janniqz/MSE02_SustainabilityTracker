package de.janniqz.sustainabilitytracker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import de.janniqz.sustainabilitytracker.data.model.entity.GoalEntity

@Dao
interface GoalDao {

    @Query("SELECT * FROM goals ORDER BY category, periodicity")
    suspend fun getAll(): List<GoalEntity>

    @Insert
    suspend fun insert(goal: GoalEntity)

    @Delete
    suspend fun delete(goal: GoalEntity)

    @Update
    suspend fun update(goal: GoalEntity)
}
