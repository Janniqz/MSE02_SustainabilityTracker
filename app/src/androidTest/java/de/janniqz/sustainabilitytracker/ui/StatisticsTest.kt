package de.janniqz.sustainabilitytracker.ui

import android.icu.util.Calendar
import androidx.navigation.fragment.NavHostFragment
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasChildCount
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.janniqz.sustainabilitytracker.MainActivity
import de.janniqz.sustainabilitytracker.R
import de.janniqz.sustainabilitytracker.data.db.AppDatabase
import de.janniqz.sustainabilitytracker.data.model.TaskCategory
import de.janniqz.sustainabilitytracker.data.model.TaskType
import de.janniqz.sustainabilitytracker.data.model.TimePeriod
import de.janniqz.sustainabilitytracker.data.model.entity.TaskCompletionEntity
import de.janniqz.sustainabilitytracker.data.model.entity.TaskEntity
import de.janniqz.sustainabilitytracker.tools.DateHelper
import de.janniqz.sustainabilitytracker.tools.FakeCalendar
import de.janniqz.sustainabilitytracker.ui.statistics.StatisticsFragment
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StatisticsTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    private lateinit var database: AppDatabase
    private lateinit var co2Task: TaskEntity
    private lateinit var waterTask: TaskEntity
    private lateinit var wasteTask: TaskEntity

    private val testToday = Calendar.getInstance().apply { clear(); set(2025, Calendar.JUNE, 18); set(Calendar.DAY_OF_WEEK, 4) } // Wednesday, June 18, 2025
    private val testLastWeek = Calendar.getInstance().apply { set(2025, Calendar.JUNE, 11) } // Wednesday, June 11, 2025
    private val testLastMonth = Calendar.getInstance().apply { set(2025, Calendar.MAY, 15) } // May 15, 2025
    private val testLastYear = Calendar.getInstance().apply { set(2024, Calendar.JULY, 1) } // July 1, 2024

    @Before
    fun setup() = runBlocking {
        database = Room.databaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
            "sustainability_database"
        ).fallbackToDestructiveMigration(false).build()
        database.clearAllTables()

        // Seed Tasks
        co2Task = TaskEntity(id = 1, name = "Carpool", type = TaskType.Predefined, category = TaskCategory.CO2, savings = 10.0f, createdAt = 0, templateId = 1)
        waterTask = TaskEntity(id = 2, name = "Short Shower", type = TaskType.Predefined, category = TaskCategory.Water, savings = 5.0f, createdAt = 0, templateId = 4)
        wasteTask = TaskEntity(id = 3, name = "Recycle", type = TaskType.Custom, category = TaskCategory.Waste, savings = 2.0f, createdAt = 0)

        // Insert tasks and get their generated IDs
        database.task().insert(co2Task)
        database.task().insert(waterTask)
        database.task().insert(wasteTask)

        // Create some Task Completions
        // Current Period (June 2025)
        database.taskCompletion().insert(TaskCompletionEntity(taskId = co2Task.id, completionTime = testToday.timeInMillis)) // CO2 - today
        database.taskCompletion().insert(TaskCompletionEntity(taskId = co2Task.id, completionTime = DateHelper.getCurrentPeriodicityRange(TimePeriod.WEEK, testToday).first)) // CO2 - start of week
        database.taskCompletion().insert(TaskCompletionEntity(taskId = waterTask.id, completionTime = testToday.timeInMillis)) // Water - today

        // Previous Week (June 11, 2025)
        database.taskCompletion().insert(TaskCompletionEntity(taskId = wasteTask.id, completionTime = testLastWeek.timeInMillis)) // Waste - last week

        // Previous Month (May 2025)
        database.taskCompletion().insert(TaskCompletionEntity(taskId = co2Task.id, completionTime = testLastMonth.timeInMillis)) // CO2 - last month
        database.taskCompletion().insert(TaskCompletionEntity(taskId = wasteTask.id, completionTime = testLastMonth.timeInMillis)) // Waste - last month

        // Previous Year (July 2024)
        database.taskCompletion().insert(TaskCompletionEntity(taskId = waterTask.id, completionTime = testLastYear.timeInMillis)) // Water - last year
        database.taskCompletion().insert(TaskCompletionEntity(taskId = waterTask.id, completionTime = testLastYear.timeInMillis + 1000)) // Water - last year (another)
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun statisticsScreen_initialLoadAndFilterSwitching_displaysCorrectData() {
        // Use a FakeCalendar so Test Dates are consistent
        val fakeCalendar = FakeCalendar(testToday)

        // Navigate to Statistics Fragment via bottom nav
        onView(withId(R.id.nav_statistics)).perform(click())

        // Inject Fake Calendar
        activityRule.scenario.onActivity { activity ->
            val navHostFragment = activity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val statisticsFragment = navHostFragment.childFragmentManager.fragments.firstOrNull { it is StatisticsFragment } as? StatisticsFragment
            statisticsFragment?.calendar = fakeCalendar
            statisticsFragment?.currentFocusDate = fakeCalendar.getCurrentCalendar()
        }

        // 1. Switch to "Week" Period (current week is June 16 - 22, 2025)
        onView(withText("Week")).perform(click())

        // Verify period text changes
        onView(withId(R.id.text_current_period)).check(matches(withText("Jun 16 - Jun 22")))
        onView(withId(R.id.text_period_savings)).check(matches(withText("20.0 kg CO₂")))
        onView(withId(R.id.completion_list)).check(matches(hasChildCount(2)))

        // 2. Switch to "Month" Period (current month is June 2025)
        onView(withText("Month")).perform(click())

        // Verify period text changes
        onView(withId(R.id.text_current_period)).check(matches(withText("June 2025")))
        onView(withId(R.id.text_period_savings)).check(matches(withText("20.0 kg CO₂")))

        // Check completions list for CO2 in June 2025 (2 items)
        onView(withId(R.id.completion_list)).check(matches(hasChildCount(2)))
        onView(withId(R.id.completion_list)).check(matches(hasDescendant(withText("Carpool"))))

        // Switch to the previous month (May 2025)
        onView(withId(R.id.btn_previous_period)).perform(click())

        // Verify period text for May 2025
        onView(withId(R.id.text_current_period)).check(matches(withText("May 2025")))
        onView(withId(R.id.text_period_savings)).check(matches(withText("10.0 kg CO₂")))
        onView(withId(R.id.completion_list)).check(matches(hasChildCount(1)))

        // 3. Switch to "Water" Category
        onView(withId(R.id.btn_water)).perform(click())
        onView(withId(R.id.text_period_savings)).check(matches(withText("0.0 liters")))
        onView(withId(R.id.completion_list)).check(matches(hasChildCount(0)))

        // 4. Navigate to "Previous Year" (July 2024)
        onView(withText("Year")).perform(click())
        onView(withId(R.id.btn_previous_period)).perform(click())

        // Verify period text for 2024
        onView(withId(R.id.text_current_period)).check(matches(withText("2024")))
        onView(withId(R.id.text_period_savings)).check(matches(withText("10.0 liters")))
        onView(withId(R.id.completion_list)).check(matches(hasChildCount(2)))
        onView(withId(R.id.completion_list)).check(matches(hasDescendant(withText("Short Shower"))))

        // 5. Navigate to "Next Period" (Current Year 2025)
        onView(withId(R.id.btn_next_period)).perform(click())
        onView(withId(R.id.text_current_period)).check(matches(withText("2025")))
        onView(withId(R.id.text_period_savings)).check(matches(withText("5.0 liters")))
        onView(withId(R.id.completion_list)).check(matches(hasChildCount(1)))
    }
}