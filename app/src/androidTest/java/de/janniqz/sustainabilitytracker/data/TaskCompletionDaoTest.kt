package de.janniqz.sustainabilitytracker.data

import android.icu.util.Calendar
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.janniqz.sustainabilitytracker.data.db.AppDatabase
import de.janniqz.sustainabilitytracker.data.db.dao.TaskCompletionDao
import de.janniqz.sustainabilitytracker.data.db.dao.TaskDao
import de.janniqz.sustainabilitytracker.data.model.TaskCategory
import de.janniqz.sustainabilitytracker.data.model.TaskType
import de.janniqz.sustainabilitytracker.data.model.entity.TaskCompletionEntity
import de.janniqz.sustainabilitytracker.data.model.entity.TaskEntity
import de.janniqz.sustainabilitytracker.tools.DateHelper.Companion.setToBeginningOfDay
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class TaskCompletionDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var taskDao: TaskDao
    private lateinit var taskCompletionDao: TaskCompletionDao

    private val testTaskId = 1 // Use a consistent ID for the parent task

    @Before
    fun setupDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        taskDao = database.task()
        taskCompletionDao = database.taskCompletion()

        // Insert a parent task that all completions can link to
        runBlocking {
            val parentTask = TaskEntity(id = testTaskId, name = "Parent Task", type = TaskType.Custom, category = TaskCategory.CO2, savings = 1f, createdAt = 0)
            taskDao.insert(parentTask)
        }
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        database.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetCountByTask_returnsCorrectCount() = runBlocking {
        // ARRANGE
        val completion1 = TaskCompletionEntity(taskId = testTaskId, completionTime = System.currentTimeMillis())
        val completion2 = TaskCompletionEntity(taskId = testTaskId, completionTime = System.currentTimeMillis() + 1000)

        // ACT
        taskCompletionDao.insert(completion1)
        taskCompletionDao.insert(completion2)

        // ASSERT
        val count = taskCompletionDao.getCountByTask(testTaskId)
        assertEquals(2, count)
    }

    @Test
    @Throws(Exception::class)
    fun getCountByTaskBetweenDates_withMixedDates_returnsCorrectCount() = runBlocking {
        // ARRANGE
        val calendar = Calendar.getInstance().apply { set(2025, Calendar.JUNE, 16) }
        val startDate = (calendar.clone() as Calendar).setToBeginningOfDay().timeInMillis
        val endDate = (calendar.clone() as Calendar).apply {
            add(Calendar.DAY_OF_MONTH, 1)
            add(Calendar.MILLISECOND, -1)
        }.timeInMillis

        val completionBefore = TaskCompletionEntity(taskId = testTaskId, completionTime = startDate - 1000)
        val completionInside1 = TaskCompletionEntity(taskId = testTaskId, completionTime = startDate + 5000)
        val completionInside2 = TaskCompletionEntity(taskId = testTaskId, completionTime = endDate - 5000)
        val completionAfter = TaskCompletionEntity(taskId = testTaskId, completionTime = endDate + 1000)

        // ACT
        taskCompletionDao.insert(completionBefore)
        taskCompletionDao.insert(completionInside1)
        taskCompletionDao.insert(completionInside2)
        taskCompletionDao.insert(completionAfter)

        // ASSERT
        val count = taskCompletionDao.getCountByTaskBetweenDates(testTaskId, startDate, endDate)
        assertEquals(2, count)
    }

    @Test
    @Throws(Exception::class)
    fun getAllByTask_withMultipleTasks_retrievesOnlyCorrectCompletions() = runBlocking {
        // ARRANGE
        val secondTaskId = 2
        val secondParentTask = TaskEntity(id = secondTaskId, name = "Second Task", type = TaskType.Custom, category = TaskCategory.Water, savings = 1f, createdAt = 0)
        taskDao.insert(secondParentTask)

        val completionForTask1 = TaskCompletionEntity(taskId = testTaskId, completionTime = 1000L)
        val completionForTask2_A = TaskCompletionEntity(taskId = secondTaskId, completionTime = 2000L)
        val completionForTask2_B = TaskCompletionEntity(taskId = secondTaskId, completionTime = 3000L)

        taskCompletionDao.insert(completionForTask1)
        taskCompletionDao.insert(completionForTask2_A)
        taskCompletionDao.insert(completionForTask2_B)

        // ACT
        val completions = taskCompletionDao.getAllByTask(secondTaskId)

        // ASSERT
        assertEquals(2, completions.size)
        assertTrue("List should not contain completion for task 1", completions.none { it.taskId == testTaskId })
        assertTrue("List should contain all completions for task 2", completions.all { it.taskId == secondTaskId })
    }

    @Test
    @Throws(Exception::class)
    fun deleteParentTask_withCascade_deletesChildCompletions() = runBlocking {
        // ARRANGE
        val completion = TaskCompletionEntity(taskId = testTaskId, completionTime = System.currentTimeMillis())
        taskCompletionDao.insert(completion)
        assertEquals(1, taskCompletionDao.getCountByTask(testTaskId))

        // ACT
        val parentTask = TaskEntity(id = testTaskId, name = "Parent Task", type = TaskType.Custom, category = TaskCategory.CO2, savings = 1f, createdAt = 0)
        taskDao.delete(parentTask)

        // ASSERT
        val countAfterDelete = taskCompletionDao.getCountByTask(testTaskId)
        assertEquals(0, countAfterDelete)
    }
}