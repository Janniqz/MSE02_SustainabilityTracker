package de.janniqz.sustainabilitytracker.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.janniqz.sustainabilitytracker.data.db.AppDatabase
import de.janniqz.sustainabilitytracker.data.db.dao.TaskDao
import de.janniqz.sustainabilitytracker.data.model.TaskCategory
import de.janniqz.sustainabilitytracker.data.model.TaskType
import de.janniqz.sustainabilitytracker.data.model.entity.TaskEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class TaskDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var taskDao: TaskDao

    @Before
    fun setupDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        taskDao = database.task()
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        database.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetAll_retrievesCorrectTask() = runBlocking {
        // ARRANGE
        val task = TaskEntity(
            name = "Carpool to Work",
            type = TaskType.Predefined,
            category = TaskCategory.CO2,
            savings = 1.5f,
            createdAt = System.currentTimeMillis(),
            templateId = 1
        )

        // ACT
        taskDao.insert(task)

        // ASSERT
        val allTasks = taskDao.getAll()
        assertEquals(1, allTasks.size)
        assertEquals("Carpool to Work", allTasks[0].name)
    }

    @Test
    @Throws(Exception::class)
    fun getAllByCategory_withMixedCategories_retrievesCorrectTasks() = runBlocking {
        // ARRANGE
        val co2Task1 = TaskEntity(name = "Carpool", type = TaskType.Predefined, category = TaskCategory.CO2, savings = 1.5f, createdAt = 0)
        val co2Task2 = TaskEntity(name = "Eat Veggie", type = TaskType.Predefined, category = TaskCategory.CO2, savings = 0.8f, createdAt = 0)
        val waterTask = TaskEntity(name = "Short Shower", type = TaskType.Predefined, category = TaskCategory.Water, savings = 20f, createdAt = 0)
        taskDao.insert(co2Task1)
        taskDao.insert(co2Task2)
        taskDao.insert(waterTask)

        // ACT
        val co2Tasks = taskDao.getAllByCategory(TaskCategory.CO2)
        val waterTasks = taskDao.getAllByCategory(TaskCategory.Water)
        val wasteTasks = taskDao.getAllByCategory(TaskCategory.Waste)

        // ASSERT
        assertEquals(2, co2Tasks.size)
        assertEquals(1, waterTasks.size)
        assertTrue(wasteTasks.isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun updateWithMultipleTasks_onlyUpdatesCorrectTask() = runBlocking {
        // ARRANGE
        val taskToUpdate = TaskEntity(id = 1, name = "Task To Update", type = TaskType.Custom, category = TaskCategory.CO2, savings = 1f, createdAt = 0)
        val taskToRemain = TaskEntity(id = 2, name = "Task To Remain Unchanged", type = TaskType.Custom, category = TaskCategory.Water, savings = 2f, createdAt = 0)
        taskDao.insert(taskToUpdate)
        taskDao.insert(taskToRemain)

        // ACT
        val updatedVersion = taskToUpdate.copy(name = "I Have Been Updated")
        taskDao.update(updatedVersion)

        // ASSERT
        val allTasks = taskDao.getAll().sortedBy { it.id }
        assertEquals(2, allTasks.size)
        assertEquals("I Have Been Updated", allTasks[0].name)
        assertEquals("Task To Remain Unchanged", allTasks[1].name)
    }

    @Test
    @Throws(Exception::class)
    fun deleteAfterInsertion_retrievesEmptyList() = runBlocking {
        // ARRANGE
        val taskToInsert = TaskEntity(name = "Test Delete", type = TaskType.Custom, category = TaskCategory.Waste, savings = 5f, createdAt = 0)
        taskDao.insert(taskToInsert)
        val insertedTask = taskDao.getAll().first()

        // ACT
        taskDao.delete(insertedTask)

        // ASSERT
        val allTasks = taskDao.getAll()
        assertTrue("The task list should be empty after deletion", allTasks.isEmpty())
    }
}