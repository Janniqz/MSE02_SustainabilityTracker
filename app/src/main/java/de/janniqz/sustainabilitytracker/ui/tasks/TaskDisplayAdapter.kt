package de.janniqz.sustainabilitytracker.ui.tasks

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import de.janniqz.sustainabilitytracker.R
import de.janniqz.sustainabilitytracker.data.model.TaskCategory
import de.janniqz.sustainabilitytracker.data.model.TaskTemplates
import de.janniqz.sustainabilitytracker.data.model.TaskType
import de.janniqz.sustainabilitytracker.data.model.TaskWithCompletions
import de.janniqz.sustainabilitytracker.data.model.entity.TaskEntity
import de.janniqz.sustainabilitytracker.databinding.ComponentTaskDisplayBinding

class TaskDisplayAdapter(context: Context, tasks: List<TaskWithCompletions>, private val taskEditFunc: (TaskEntity) -> Unit, private val taskCompleteFunc: (TaskEntity) -> Unit, private val taskDeleteFunc: (TaskEntity) -> Unit) : ArrayAdapter<TaskWithCompletions>(context, 0, tasks) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = if (convertView == null) {
            ComponentTaskDisplayBinding.inflate(LayoutInflater.from(context), parent, false)
        } else {
            ComponentTaskDisplayBinding.bind(convertView)
        }

        val taskData = getItem(position)!!
        val task = taskData.task
        val icon = getTaskIcon(task.category)

        val completions = taskData.completions
        val savings = task.savings * completions
        val savingsUnit = context.getString(getSavingsUnit(task.category))

        binding.taskName.text = task.name
        binding.taskCategoryIcon.setImageResource(icon)

        if (task.type == TaskType.Predefined) {
            val template = TaskTemplates.getTemplateById(task.templateId!!)!!
            binding.taskDescription.text = template.description
        } else {
            binding.taskDescription.text = ""
        }

        binding.taskSavings.text = context.getString(R.string.task_display_savings, savings, savingsUnit)
        binding.taskCompletions.text = context.getString(R.string.task_display_completions, completions)

        binding.buttonEdit.setOnClickListener { taskEditFunc(task) }
        binding.buttonComplete.setOnClickListener { taskCompleteFunc(task) }
        binding.buttonDelete.setOnClickListener { taskDeleteFunc(task) }

        return binding.root
    }

    private fun getTaskIcon(category: TaskCategory): Int {
        return when (category) {
            TaskCategory.CO2 -> R.drawable.ic_category_co2
            TaskCategory.Water -> R.drawable.ic_category_water
            TaskCategory.Waste -> R.drawable.ic_category_waste
        }
    }

    private fun getSavingsUnit(category: TaskCategory): Int {
        return when (category) {
            TaskCategory.CO2 -> R.string.task_category_co2_unit
            TaskCategory.Water -> R.string.task_category_water_unit
            TaskCategory.Waste -> R.string.task_category_waste_unit
        }
    }
}
