package de.janniqz.sustainabilitytracker.ui.statistics

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import de.janniqz.sustainabilitytracker.R
import de.janniqz.sustainabilitytracker.data.model.StatisticsCompletion
import de.janniqz.sustainabilitytracker.data.model.TaskCategory
import de.janniqz.sustainabilitytracker.databinding.ComponentStatisticsCompletionDisplayBinding
import java.util.Date
import java.util.Locale

/**
 * List Adapter responsible for displaying Statistics Completions
 */
class StatisticsCompletionDisplayAdapter(context: Context, tasks: List<StatisticsCompletion>) : ArrayAdapter<StatisticsCompletion>(context, 0, tasks) {

    // Date Time Formatter for the Completion Date
    private val dateTimeFormat = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())

    /**
     * Initializes a Statistic Completion Display
     */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = if (convertView == null) {
            ComponentStatisticsCompletionDisplayBinding.inflate(LayoutInflater.from(context), parent, false)
        } else {
            ComponentStatisticsCompletionDisplayBinding.bind(convertView)
        }

        val completionData = getItem(position)!!
        val task = completionData.task
        val completion = completionData.completion

        val icon = TaskCategory.getTaskIcon(task.category)
        val savingsUnit = TaskCategory.getSavingsUnit(task.category)

        binding.completionTime.text = dateTimeFormat.format(Date(completion.completionTime))
        binding.completionTaskName.text = task.name
        binding.completionSavings.text = context.getString(R.string.statistics_display_savings, task.savings, context.getString(savingsUnit))
        binding.completionCategoryIcon.setImageResource(icon)

        return binding.root
    }
}
