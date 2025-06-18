package de.janniqz.sustainabilitytracker.ui

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.janniqz.sustainabilitytracker.MainActivity
import de.janniqz.sustainabilitytracker.R
import de.janniqz.sustainabilitytracker.data.db.AppDatabase
import org.hamcrest.CoreMatchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateTaskTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun clearDatabase() {
        val db = Room.databaseBuilder(
                ApplicationProvider.getApplicationContext(),
                AppDatabase::class.java,
                "sustainability_database" // Use the same name as your production DB
            ).fallbackToDestructiveMigration(false).build()
        db.clearAllTables()
        db.close()
    }

    @Test
    fun createCustomTask_verifyCreation() {
        val customTaskName = "Use Reusable Coffee Cup"

        // 1. Click "New Task" FAB
        onView(withId(R.id.fab_new_task)).perform(click())

        // 2. Click Custom Task
        onView(withText(R.string.task_type_custom_title)).perform(click())

        // 3. Creation Dialog should now be visible.
        onView(withText(R.string.task_type_custom_title)).check(matches(isDisplayed()))

        // 4. Fill in Task Name
        onView(allOf(
            isDescendantOfA(withId(R.id.input_task_name)),
            withId(R.id.input_field)
        )).perform(typeText(customTaskName))

        // Switch to Waste category
        onView(withId(R.id.btn_waste)).perform(click())

        // Fill in savings
        onView(allOf(
            isDescendantOfA(withId(R.id.input_task_saving)),
            withId(R.id.input_field)
        )).perform(typeText("0.1"))

        // 5. Create Task
        onView(withId(R.id.btn_submit)).perform(click())

        // 6. Check if Task present in list (via name)
        onView(withText(customTaskName)).check(matches(isDisplayed()))
    }

    @Test
    fun createPredefinedTask_verifyCreation() {
        val predefinedTaskName = "My Daily Carpool"

        // 1. Click "New Task" FAB
        onView(withId(R.id.fab_new_task)).perform(click())

        // 2. Click Predefined Task
        onView(withText(R.string.task_type_predefined_title)).perform(click())

        // 3. Select Carpooling Template
        onView(allOf(
            withText("Carpooling"),
            isDisplayed()
        )).perform(click())

        // 4. Check if Dialog is displayed
        onView(withText("Create Task")).check(matches(isDisplayed()))

        // 5. Fill in name
        onView(allOf(
            isDescendantOfA(withId(R.id.input_task_name)),
            withId(R.id.input_field)
        )).perform(typeText(predefinedTaskName))

        // 6. Fill in value
        onView(allOf(
            isDescendantOfA(withId(R.id.container_required_data)),
            withId(R.id.input_field)
        )).perform(typeText("10.5"))

        // 7. Create Task
        onView(withId(R.id.btn_submit)).perform(click())

        // 8. Check if Task present in list (via name)
        onView(withText(predefinedTaskName)).check(matches(isDisplayed()))
    }

    @Test
    fun createTask_testValidation() {
        // 1. Click "New Task" FAB
        onView(withId(R.id.fab_new_task)).perform(click())

        // 2. Click Custom Task
        onView(withText(R.string.task_type_custom_title)).perform(click())

        // 3. Creation Dialog should now be visible.
        onView(withText(R.string.task_type_custom_title)).check(matches(isDisplayed()))

        // 4. Try to Create Task
        onView(withId(R.id.btn_submit)).perform(click())

        // 5. Check if error messages are being displayed
        onView(withId(R.id.input_task_name)).check(matches(hasDescendant(withText(R.string.general_required))))
        onView(withId(R.id.input_task_saving)).check(matches(hasDescendant(withText(R.string.general_required))))

        // 6. Verify the dialog is *still* displayed (i.e., it did NOT dismiss)
        onView(withText(R.string.task_type_custom_title)).check(matches(isDisplayed()))

        // 7. Verify that the FAB is not being displayed (didn't return to Task List)
        onView(withId(R.id.fab_new_task)).check(doesNotExist())
    }
}