package de.janniqz.sustainabilitytracker.ui

import android.icu.util.Calendar
import androidx.navigation.fragment.NavHostFragment
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.janniqz.sustainabilitytracker.GoalActivity
import de.janniqz.sustainabilitytracker.R
import de.janniqz.sustainabilitytracker.data.db.AppDatabase
import de.janniqz.sustainabilitytracker.data.model.TaskCategory
import de.janniqz.sustainabilitytracker.data.model.TaskType
import de.janniqz.sustainabilitytracker.data.model.TimePeriod
import de.janniqz.sustainabilitytracker.data.model.entity.GoalEntity
import de.janniqz.sustainabilitytracker.data.model.entity.TaskCompletionEntity
import de.janniqz.sustainabilitytracker.data.model.entity.TaskEntity
import de.janniqz.sustainabilitytracker.ui.goals.GoalsFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class GoalTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(GoalActivity::class.java)

    private lateinit var database: AppDatabase
    private lateinit var co2Task: TaskEntity

    @Before
    fun setupDatabase() = runBlocking {
        database = Room.databaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
            "sustainability_database"
        ).fallbackToDestructiveMigration(false).build()
        database.clearAllTables()

        // Seed a task and completions, as goals rely on task data for progress
        co2Task = TaskEntity(id = 1, name = "Test CO2 Task", type = TaskType.Custom, category = TaskCategory.CO2, savings = 5.0f, createdAt = System.currentTimeMillis())
        database.task().insert(co2Task)

        // Add 2 completions for the current week (for progress calculation)
        val today = Calendar.getInstance()
        database.taskCompletion().insert(TaskCompletionEntity(taskId = co2Task.id, completionTime = today.timeInMillis - (1 * 24 * 60 * 60 * 1000))) // Yesterday
        database.taskCompletion().insert(TaskCompletionEntity(taskId = co2Task.id, completionTime = today.timeInMillis)) // Today
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun createGoal_success() {
        // ARRANGE
        val initialGoalName = "Reduce Weekly Carbon"
        val initialTarget = "50.0"

        // ACT
        onView(withId(R.id.fab_new_goal)).perform(click())

        // Verify the Create Goal dialog title is displayed
        onView(withId(R.id.dialog_goal_title)).check(matches(isDisplayed()))

        // Enter goal name
        onView(allOf(
            isDescendantOfA(withId(R.id.input_goal_name)),
            withId(R.id.input_field)
        )).perform(typeText(initialGoalName))

        // Change to WEEK periodicity for specific test data context (default is MONTH)
        onView(withId(R.id.btn_week)).perform(click())

        // Enter target amount
        onView(allOf(
            isDescendantOfA(withId(R.id.input_goal_target)),
            withId(R.id.input_field)
        )).perform(typeText(initialTarget))

        // Click Save/Create Goal button
        onView(withId(R.id.btn_submit)).perform(click())

        // ASSERT
        // Verify the new goal appears in the list
        onView(withId(R.id.goal_list)).check(matches(hasDescendant(withText(initialGoalName))))

        // Verify initial progress for a WEEK goal: 2 completions * 5.0 savings = 10.0
        // Default category in dialog is CO2, test task is CO2. Period is WEEK.
        onView(allOf(
            withText(containsString("Achieved: 10.0 / 50.0 kg CO₂")),
            isDescendantOfA(withId(R.id.goal_list))
        )).check(matches(isDisplayed()))
    }

    @Test
    fun editGoal_success() = runTest {
        // ARRANGE
        val originalGoalName = "Initial Weekly Goal"
        val originalTarget = 100.0f
        val originalGoal = GoalEntity(
            name = originalGoalName,
            category = TaskCategory.CO2,
            periodicity = TimePeriod.WEEK,
            targetAmount = originalTarget
        )
        database.goal().insert(originalGoal)
        activityRule.scenario.onActivity { activity ->
            val navHostFragment = activity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val goalsFragment = navHostFragment.childFragmentManager.fragments.firstOrNull { it is GoalsFragment } as? GoalsFragment
            goalsFragment?.loadGoals() // Manually trigger load after data is in DB
        }
        advanceUntilIdle()

        // ACT
        val updatedGoalName = "My Edited Goal"
        val updatedTarget = "75.0" // Target will actually be lowered for this example

        // Click the Edit button for the goal
        onView(allOf(
            withId(R.id.btn_edit), // The ID of the edit button in item_goal.xml
            isDisplayed()
        )).perform(click())

        // Verify the Edit Goal dialog title is displayed
        onView(allOf(
            withId(R.id.dialog_goal_title),
            withText(R.string.goal_edit))).check(matches(isDisplayed()))

        // Update goal name
        onView(allOf(
            isDescendantOfA(withId(R.id.input_goal_name)),
            withId(R.id.input_field)
        )).perform(replaceText(updatedGoalName))

        // Update target amount
        onView(allOf(
            isDescendantOfA(withId(R.id.input_goal_target)),
            withId(R.id.input_field)
        )).perform(replaceText(updatedTarget))

        // Change category to Water
        onView(withId(R.id.btn_water)).perform(click())

        // Change periodicity to Month
        onView(withId(R.id.btn_month)).perform(click())

        // Click Save/Update Goal button
        onView(withId(R.id.btn_submit)).perform(click())

        // ASSERT
        // Verify the updated goal appears in the list with new name
        onView(withId(R.id.goal_list)).check(matches(hasDescendant(withText(updatedGoalName))))

        // Verify updated target and periodicity. Progress will be 0 as water tasks completed only CO2 in seeded data
        onView(allOf(
            withText(containsString("Target: ${"%.1f".format(updatedTarget.toFloat())} liters / Month")),
            isDescendantOfA(withId(R.id.goal_list))
        )).check(matches(isDisplayed()))

        onView(allOf(
            withText(containsString("Achieved: 0.0 / ${"%.1f".format(updatedTarget.toFloat())} liters")),
            isDescendantOfA(withId(R.id.goal_list))
        )).check(matches(isDisplayed()))
    }


    @Test
    fun deleteGoal_success() = runTest {
        // ARRANGE
        val goalToDeleteName = "Goal to be Deleted"
        val goalToDelete = GoalEntity(
            name = goalToDeleteName,
            category = TaskCategory.Waste,
            periodicity = TimePeriod.YEAR,
            targetAmount = 10f
        )

        database.goal().insert(goalToDelete)
        activityRule.scenario.onActivity { activity ->
            val navHostFragment = activity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val goalsFragment = navHostFragment.childFragmentManager.fragments.firstOrNull { it is GoalsFragment } as? GoalsFragment
            goalsFragment?.loadGoals() // Manually trigger load after data is in DB
        }
        advanceUntilIdle()

        // ACT
        onView(allOf(
            withId(R.id.btn_delete), // The ID of the delete button in item_goal.xml
            isDisplayed()
        )).perform(click())

        // Verify Delete Confirmation dialog is displayed
        onView(allOf(withId(R.id.confirmation_name), withText(R.string.goal_delete))).check(matches(isDisplayed()))

        // Click Confirm Delete button
        onView(withId(R.id.btn_confirm)).perform(click())

        // ASSERT
        onView(withId(R.id.goal_list)).check(matches(not(hasDescendant(withText(goalToDeleteName)))))
    }
}