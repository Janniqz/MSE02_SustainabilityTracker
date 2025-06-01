package de.janniqz.sustainabilitytracker.ui.statistics

import AppDatabase
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayout
import de.janniqz.sustainabilitytracker.R
import de.janniqz.sustainabilitytracker.data.model.StatisticsCompletion
import de.janniqz.sustainabilitytracker.data.model.TaskCategory
import de.janniqz.sustainabilitytracker.data.model.TimePeriod
import de.janniqz.sustainabilitytracker.databinding.FragmentStatisticsBinding
import kotlinx.coroutines.launch
import java.util.Locale

class StatisticsFragment : Fragment() {

    private lateinit var binding: FragmentStatisticsBinding
    private lateinit var completionListAdapter: StatisticsCompletionDisplayAdapter
    private lateinit var database: AppDatabase

    private var selectedTimePeriod: TimePeriod = TimePeriod.MONTH
    private var selectedCategory: TaskCategory = TaskCategory.CO2
    private var currentFocusDate: Calendar = Calendar.getInstance()

    private var startDate: Long = -1L
    private var endDate: Long = -1L

    private val shortDateFormat = SimpleDateFormat("MMM d", Locale.getDefault())
    private val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    private val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())

    private var currentCompletions: MutableList<StatisticsCompletion> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        database = AppDatabase.getInstance(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTimePeriodActions()
        setupCategoryActions()

        completionListAdapter = StatisticsCompletionDisplayAdapter(requireContext(), currentCompletions)
        binding.completionList.adapter = completionListAdapter

        calculateCurrentPeriodDates()
        updatePeriodText()
        updateSavingsIcon()
        updateCompletions()
    }

    // region Time Period Handling

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

    private fun switchTimePeriodType(newPeriodType: TimePeriod) {
        if (selectedTimePeriod == newPeriodType)
            return

        selectedTimePeriod = newPeriodType
        currentFocusDate = Calendar.getInstance()
        calculateCurrentPeriodDates()
        updatePeriodText()
        updateCompletions()
    }

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

    private fun calculateCurrentPeriodDates() {
        val calendar = currentFocusDate.clone() as Calendar

        // Reset Calendar fields to start of day
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        when (selectedTimePeriod) {
            TimePeriod.WEEK -> {
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                startDate = calendar.timeInMillis

                calendar.add(Calendar.WEEK_OF_YEAR, 1)
                calendar.add(Calendar.MILLISECOND, -1) // End of the last day of the week
                endDate = calendar.timeInMillis
            }
            TimePeriod.MONTH -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                startDate = calendar.timeInMillis

                calendar.add(Calendar.MONTH, 1)
                calendar.add(Calendar.MILLISECOND, -1) // End of the last day of the month
                endDate = calendar.timeInMillis
            }
            TimePeriod.YEAR -> {
                calendar.set(Calendar.DAY_OF_YEAR, 1)
                startDate = calendar.timeInMillis

                calendar.add(Calendar.YEAR, 1)
                calendar.add(Calendar.MILLISECOND, -1) // End of the last day of the year
                endDate = calendar.timeInMillis
            }
        }
    }

    private fun updatePeriodText() {
        val startCal = Calendar.getInstance().apply { timeInMillis = startDate }
        val endCal = Calendar.getInstance().apply { timeInMillis = endDate }

        val periodText = when (selectedTimePeriod) {
            TimePeriod.WEEK -> "${shortDateFormat.format(startCal.time)} - ${shortDateFormat.format(endCal.time)}"
            TimePeriod.MONTH -> monthYearFormat.format(startCal.time)
            TimePeriod.YEAR -> yearFormat.format(startCal.time)
        }
        binding.textCurrentPeriod.text = periodText
    }

    // endregion

    // region Category Handling

    private fun setupCategoryActions() {
        binding.statsCategorySelection.btnCo2.setOnClickListener { switchCategory(TaskCategory.CO2) }
        binding.statsCategorySelection.btnWater.setOnClickListener { switchCategory(TaskCategory.Water) }
        binding.statsCategorySelection.btnWaste.setOnClickListener { switchCategory(TaskCategory.Waste) }

        binding.statsCategorySelection.btnCo2.isChecked = selectedCategory == TaskCategory.CO2
    }

    private fun switchCategory(category: TaskCategory) {
        if (selectedCategory == category)
            return

        selectedCategory = category

        updateSavingsIcon()
        updateCompletions()
    }

    // endregion

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
            currentCompletions.addAll(completions)
            completionListAdapter.notifyDataSetChanged()

            updateSavingsText(totalSavings)
        }
    }

    private fun updateSavingsText(savings: Float) {
        binding.textPeriodSavings.text = getString(R.string.statistics_savings, savings, getString(TaskCategory.getSavingsUnit(selectedCategory)))
    }

    private fun updateSavingsIcon() {
        binding.textPeriodSavings.setCompoundDrawablesWithIntrinsicBounds(TaskCategory.getTaskIcon(selectedCategory), 0, 0, 0)
    }
}