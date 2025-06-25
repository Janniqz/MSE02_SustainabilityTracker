package de.janniqz.sustainabilitytracker.ui.statistics

import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.tabs.TabLayout
import de.janniqz.sustainabilitytracker.R
import de.janniqz.sustainabilitytracker.data.db.AppDatabase
import de.janniqz.sustainabilitytracker.data.model.StatisticsCompletion
import de.janniqz.sustainabilitytracker.data.model.TaskCategory
import de.janniqz.sustainabilitytracker.data.model.TimePeriod
import de.janniqz.sustainabilitytracker.databinding.FragmentStatisticsBinding
import de.janniqz.sustainabilitytracker.tools.DateHelper
import de.janniqz.sustainabilitytracker.tools.DateHelper.Companion.setToBeginningOfDay
import de.janniqz.sustainabilitytracker.tools.ICalendar
import de.janniqz.sustainabilitytracker.tools.SystemCalendar
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * Fragment displaying Statistics about Task Completions
 */
class StatisticsFragment : Fragment() {

    var calendar: ICalendar = SystemCalendar()
    var currentFocusDate: Calendar = calendar.getCurrentCalendar()

    private lateinit var binding: FragmentStatisticsBinding
    private lateinit var completionListAdapter: StatisticsCompletionDisplayAdapter
    private lateinit var database: AppDatabase

    private var selectedTimePeriod: TimePeriod = TimePeriod.MONTH
    private var selectedCategory: TaskCategory = TaskCategory.CO2

    private var startDate: Long = -1L
    private var endDate: Long = -1L

    private val shortDateFormat = SimpleDateFormat("MMM d", Locale.getDefault())                    // Date Time Formatter for Weekly Period
    private val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())                // Date Time Formatter for Monthly Period
    private val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())                          // Date Time Formatter for Yearly Period
    private val dayOfMonthFormat = SimpleDateFormat("d", Locale.getDefault())                       // Date Time Formatter for Monthly Period Labels (Graph)

    private var currentCompletions: MutableList<StatisticsCompletion> = mutableListOf()

    /**
     * Retrieves Binding and Database references on Fragment Creation
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        database = AppDatabase.getInstance(requireContext())
        return binding.root
    }

    /**
     * Initializes UI and List Adapter.
     * Retrieves the initial list of Completions with the default Category + Periodicity.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTimePeriodActions()
        setupCategoryActions()
        setupChart()

        completionListAdapter = StatisticsCompletionDisplayAdapter(requireContext(), currentCompletions)
        binding.completionList.adapter = completionListAdapter

        calculateCurrentPeriodDates()
        updatePeriodText()
        updateSavingsIcon()
        updateCompletions()
    }

    // region Time Period Handling

    /**
     * Adds Event Listeners for Period Tabs + Buttons
     */
    private fun setupTimePeriodActions() {
        binding.periodTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    val newPeriod = when (it.position) {
                        0 -> TimePeriod.WEEK
                        1 -> TimePeriod.MONTH
                        2 -> TimePeriod.YEAR
                        else -> TimePeriod.MONTH // Default
                    }
                    switchTimePeriodType(newPeriod)
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.periodTabs.getTabAt(selectedTimePeriod.ordinal)?.select()
        binding.btnPreviousPeriod.setOnClickListener { switchTimePeriod(-1) }
        binding.btnNextPeriod.setOnClickListener { switchTimePeriod(1) }
    }

    /**
     * Updates the currently selected TimePeriod.
     * Recalculates displayed Time Range and reloads Completions.
     */
    private fun switchTimePeriodType(newPeriodType: TimePeriod) {
        if (selectedTimePeriod == newPeriodType)
            return

        selectedTimePeriod = newPeriodType
        currentFocusDate = calendar.getCurrentCalendar()
        calculateCurrentPeriodDates()
        updatePeriodText()
        updateCompletions()
    }

    /**
     * Switches the current Time Range back / forward by one period.
     * Recalculates displayed Time Range and reloads Completions.
     */
    private fun switchTimePeriod(direction: Int) { // -1 for previous, 1 for next
        when (selectedTimePeriod) {
            TimePeriod.WEEK -> currentFocusDate.add(Calendar.WEEK_OF_YEAR, direction)
            TimePeriod.MONTH -> currentFocusDate.add(Calendar.MONTH, direction)
            TimePeriod.YEAR -> currentFocusDate.add(Calendar.YEAR, direction)
        }

        calculateCurrentPeriodDates()
        updatePeriodText()
        updateCompletions()
    }

    /**
     * Updates the displayed Time Range
     */
    private fun calculateCurrentPeriodDates() {
        val period = DateHelper.getCurrentPeriodicityRange(selectedTimePeriod, currentFocusDate)
        startDate = period.first
        endDate = period.second
    }

    /**
     * Updates the UI Elements displaying the current Time Range
     */
    private fun updatePeriodText() {
        val startCal = calendar.getCurrentCalendar().apply { timeInMillis = startDate }
        val endCal = calendar.getCurrentCalendar().apply { timeInMillis = endDate }

        val periodText = when (selectedTimePeriod) {
            TimePeriod.WEEK -> "${shortDateFormat.format(startCal.time)} - ${shortDateFormat.format(endCal.time)}"
            TimePeriod.MONTH -> monthYearFormat.format(startCal.time)
            TimePeriod.YEAR -> yearFormat.format(startCal.time)
        }
        binding.textCurrentPeriod.text = periodText
    }

    // endregion

    // region Category Handling

    /**
     * Sets up Event Listeners for the Category Buttons
     */
    private fun setupCategoryActions() {
        binding.statsCategorySelection.btnCo2.setOnClickListener { switchCategory(TaskCategory.CO2) }
        binding.statsCategorySelection.btnWater.setOnClickListener { switchCategory(TaskCategory.Water) }
        binding.statsCategorySelection.btnWaste.setOnClickListener { switchCategory(TaskCategory.Waste) }

        // Only relevant for the initial selection
        binding.statsCategorySelection.btnCo2.isChecked = selectedCategory == TaskCategory.CO2
    }

    /**
     * Switches the currently displayed Category of Task Completions.
     * Updates the Saving Icon and reloads completions.
     */
    private fun switchCategory(category: TaskCategory) {
        if (selectedCategory == category)
            return

        selectedCategory = category

        updateSavingsIcon()
        updateCompletions()
    }

    // endregion

    // region Bar Chart Handling

    /**
     * Base Setup for the Bar Chart.
     * Sets up which Axis are being displayed and in which way.
     */
    private fun setupChart() {
        binding.savingsChart.apply {
            description.isEnabled = false
            isDoubleTapToZoomEnabled = false

            setDrawGridBackground(false)
            setDrawBarShadow(false)
            setPinchZoom(false)
            setScaleEnabled(false)
            setTouchEnabled(false)

            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.textColor = Color.WHITE
            xAxis.granularity = 1f
            xAxis.isGranularityEnabled = true
            xAxis.setDrawGridLines(false)

            axisRight.isEnabled = true
            axisRight.axisMinimum = 0f
            axisRight.textColor = Color.WHITE
            axisRight.setDrawGridLines(true)

            axisLeft.isEnabled = false
            legend.isEnabled = false
        }
    }

    /**
     * Update the displayed bars / labels of the Bar Chart
     */
    private fun updateChart() {
        val barEntries = ArrayList<BarEntry>()
        val axisLabels = ArrayList<String>()

        when (selectedTimePeriod) {
            TimePeriod.WEEK -> getWeeklyChartEntries(barEntries, axisLabels)
            TimePeriod.MONTH -> getMonthlyChartEntries(barEntries, axisLabels)
            TimePeriod.YEAR -> getYearlyChartEntries(barEntries, axisLabels)
        }

        val barDataSet = BarDataSet(barEntries, "Savings")
        barDataSet.color = Color.WHITE
        barDataSet.valueTextColor = Color.WHITE
        barDataSet.valueTextSize = 10f
        barDataSet.axisDependency = YAxis.AxisDependency.RIGHT
        barDataSet.setDrawValues(false)  // Don't draw values on top of bars

        // Adjust the Bar Width depending on the selected Periodicity to accommodate different numbers of bars
        val barData = BarData(barDataSet)
        barData.barWidth = when(selectedTimePeriod) {
            TimePeriod.WEEK -> 0.6f
            TimePeriod.MONTH -> 0.8f
            TimePeriod.YEAR -> 0.5f
        }

        binding.savingsChart.data = barData
        binding.savingsChart.xAxis.valueFormatter = IndexAxisValueFormatter(axisLabels)
        binding.savingsChart.xAxis.labelCount = axisLabels.size

        var yMax = barEntries.maxOfOrNull { it.y } ?: 0f
        if (yMax > 1f)
            yMax = yMax * 1.1f  // Add 10% padding to the max value
        else
            yMax = 1.0f // Default max to 1.0 if no / small values

        binding.savingsChart.axisRight.axisMaximum = yMax
        binding.savingsChart.invalidate() // Refresh the chart
    }

    /**
     * Retrieves Bar Entries and Bar Labels for the Monthly Periodicity.
     * A bar is added for every day of the week. Labels reflect the day in the month.
     */
    private fun getWeeklyChartEntries(barEntries: ArrayList<BarEntry>, labels: ArrayList<String>) {
        val weekCalendar = calendar.getCurrentCalendar()
        weekCalendar.timeInMillis = startDate

        // Loop through all days of the week
        for (i in 0 until 7) {
            val dayStart = weekCalendar.timeInMillis
            weekCalendar.add(Calendar.DAY_OF_MONTH, 1)
            val dayEnd = weekCalendar.timeInMillis - 1

            val savingsForDay = getTotalSavingsBetween(dayStart, dayEnd)
            barEntries.add(BarEntry(i.toFloat(), savingsForDay))

            // Day in month
            val dayLabelCalendar = calendar.getCurrentCalendar().apply { timeInMillis = dayStart }
            labels.add(dayOfMonthFormat.format(dayLabelCalendar.time))
        }
    }

    /**
     * Retrieves Bar Entries and Bar Labels for the Monthly Periodicity.
     * A bar is added for every first, 5th, and last day of the month.
     */
    private fun getMonthlyChartEntries(barEntries: ArrayList<BarEntry>, labels: ArrayList<String>) {
        val monthCalendar = calendar.getCurrentCalendar().apply { timeInMillis = startDate }
        val daysInMonth = monthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        // Loop through all days of the month
        for (i in 0 until daysInMonth) {
            val currentDayCal = calendar.getCurrentCalendar().apply {
                timeInMillis = startDate
                add(Calendar.DAY_OF_MONTH, i)
            }.setToBeginningOfDay()

            val dayStart = currentDayCal.timeInMillis
            currentDayCal.add(Calendar.DAY_OF_MONTH, 1)
            val dayEnd = currentDayCal.timeInMillis - 1

            val savingsForDay = getTotalSavingsBetween(dayStart, dayEnd)
            barEntries.add(BarEntry(i.toFloat(), savingsForDay))

            // Only show every other day to not clutter the x-axis
            // Shows the first day, every 5th following day, and the last day of the month
            val dayOfMonth = i + 1
            if (dayOfMonth == 1 || dayOfMonth % 5 == 1 || dayOfMonth == daysInMonth) {
                labels.add(dayOfMonth.toString())
            } else {
                labels.add("") // Empty label for other days
            }
        }
    }

    /**
     * Retrieves Bar Entries and Bar Labels for the Yearly Periodicity.
     * A bar is added for every month of the Year.
     */
    private fun getYearlyChartEntries(barEntries: ArrayList<BarEntry>, labels: ArrayList<String>) {
        // Loop through all months of the year
        for (i in 0 until 12) {
            val monthStartCal = calendar.getCurrentCalendar().apply {
                timeInMillis = startDate
                add(Calendar.MONTH, i)
                set(Calendar.DAY_OF_MONTH, 1)
            }.setToBeginningOfDay()

            val monthStart = monthStartCal.timeInMillis
            val monthEndCal = monthStartCal.clone() as Calendar
            monthEndCal.add(Calendar.MONTH, 1)
            monthEndCal.add(Calendar.MILLISECOND, -1)
            val monthEnd = monthEndCal.timeInMillis

            val savingsForMonth = getTotalSavingsBetween(monthStart, monthEnd)
            barEntries.add(BarEntry(i.toFloat(), savingsForMonth))

            // Show every odd month
            val monthNumber = i + 1
            if (monthNumber % 2 != 0) {
                labels.add(monthNumber.toString())
            } else {
                labels.add("")
            }
        }
    }

    /**
     * Calculates the total savings in the given Time Range
     */
    private fun getTotalSavingsBetween(timeStart: Long, timeEnd: Long): Float {
        var totalSavings = 0f
        for (completion in currentCompletions.filter { it.completion.completionTime in timeStart..timeEnd }) {
            totalSavings += completion.task.savings
        }

        return totalSavings
    }

    // endregion

    /**
     * Reloads Statistics Completions depending on the currently selected TaskCategory and Time Range.
     * Completions are sorted by their completion time in descending order.
     */
    private fun updateCompletions() {
        lifecycleScope.launch {
            val completions = mutableListOf<StatisticsCompletion>()
            var totalSavings = 0f;

            val categoryTasks = database.task().getAllByCategory(selectedCategory)
            for (categoryTask in categoryTasks) {
                val taskCompletions = database.taskCompletion().getAllByTaskBetweenDates(categoryTask.id, startDate, endDate)
                for (completion in taskCompletions) {
                    completions.add(StatisticsCompletion(categoryTask, completion))
                    totalSavings += categoryTask.savings
                }
            }

            currentCompletions.clear()
            currentCompletions.addAll(completions.sortedByDescending { it.completion.completionTime })
            completionListAdapter.notifyDataSetChanged()

            updateSavingsText(totalSavings)
            updateChart()
        }
    }

    /**
     * Update the Savings Text depending on the current Savings + Task Category
     */
    private fun updateSavingsText(savings: Float) {
        binding.textPeriodSavings.text = getString(R.string.statistics_savings, savings, getString(TaskCategory.getSavingsUnit(selectedCategory)))
    }

    /**
     * Update the displayed Savings Icon depending on the currently selected TaskCategory
     */
    private fun updateSavingsIcon() {
        binding.textPeriodSavings.setCompoundDrawablesWithIntrinsicBounds(TaskCategory.getTaskIcon(selectedCategory), 0, 0, 0)
    }
}