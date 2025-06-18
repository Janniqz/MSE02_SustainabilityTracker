package de.janniqz.sustainabilitytracker.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.janniqz.sustainabilitytracker.data.db.AppDatabase
import de.janniqz.sustainabilitytracker.data.db.dao.GoalDao
import de.janniqz.sustainabilitytracker.data.model.TaskCategory
import de.janniqz.sustainabilitytracker.data.model.TimePeriod
import de.janniqz.sustainabilitytracker.data.model.entity.GoalEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class GoalDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var goalDao: GoalDao

    @Before
    fun setupDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        goalDao = database.goal()
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        database.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetAllGoals_retrievesCorrectGoal() = runBlocking {
        // ARRANGE
        val goal = GoalEntity(
            id = 1,
            name = "Weekly CO2 Goal",
            category = TaskCategory.CO2,
            periodicity = TimePeriod.WEEK,
            targetAmount = 50f
        )

        // ACT
        goalDao.insert(goal)

        // ASSERT
        val allGoals = goalDao.getAll()
        assertEquals(1, allGoals.size)
        assertEquals("Weekly CO2 Goal", allGoals[0].name)
    }

    @Test
    @Throws(Exception::class)
    fun updateAndGetAllGoals_retrievesUpdatedGoal() = runBlocking {
        // ARRANGE
        val originalGoal = GoalEntity(
            id = 1,
            name = "Monthly Water Goal",
            category = TaskCategory.Water,
            periodicity = TimePeriod.MONTH,
            targetAmount = 1000f
        )
        goalDao.insert(originalGoal)

        // ACT
        val updatedGoal = originalGoal.copy(name = "Updated Water Goal", targetAmount = 1200f)
        goalDao.update(updatedGoal)

        // ASSERT
        val allGoals = goalDao.getAll()
        assertEquals(1, allGoals.size)
        assertEquals("Updated Water Goal", allGoals[0].name)
        assertEquals(1200f, allGoals[0].targetAmount)
    }

    @Test
    @Throws(Exception::class)
    fun deleteAfterInsertion_retrievesEmptyList() = runBlocking {
        // ARRANGE
        val goalToInsert = GoalEntity(
            name = "Yearly Waste Goal",
            category = TaskCategory.Waste,
            periodicity = TimePeriod.YEAR,
            targetAmount = 20f
        )
        goalDao.insert(goalToInsert)
        val insertedGoal = goalDao.getAll().first()

        // ACT
        goalDao.delete(insertedGoal)

        // ASSERT
        val allGoals = goalDao.getAll()
        assertTrue("The goal list should be empty after deletion", allGoals.isEmpty())
    }
}