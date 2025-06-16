package de.janniqz.sustainabilitytracker.ui.tasks

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import de.janniqz.sustainabilitytracker.R
import de.janniqz.sustainabilitytracker.data.model.TaskCategory
import de.janniqz.sustainabilitytracker.data.model.TaskTemplate
import de.janniqz.sustainabilitytracker.databinding.ComponentTaskTypeButtonBinding

/**
 * List Adapter responsible for displaying Task Templates
 */
class TaskTemplateAdapter(context: Context, taskTemplates: List<TaskTemplate>, private val taskClickFunc: (TaskTemplate) -> Unit) : ArrayAdapter<TaskTemplate>(context, 0, taskTemplates) {

    /**
     * Initializes a Task Template Display
     */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = if (convertView == null) {
            ComponentTaskTypeButtonBinding.inflate(LayoutInflater.from(context), parent, false)
        } else {
            ComponentTaskTypeButtonBinding.bind(convertView)
        }

        val taskTemplate = getItem(position)!!
        val icon = when (taskTemplate.category) {
            TaskCategory.CO2 -> R.drawable.ic_category_co2
            TaskCategory.Water -> R.drawable.ic_category_water
            TaskCategory.Waste -> R.drawable.ic_category_waste
        }

        binding.btnTitle.text = taskTemplate.name
        binding.btnDesc.text = taskTemplate.description
        binding.btnIcon.setImageResource(icon)
        binding.btnRoot.setOnClickListener {
            taskClickFunc(taskTemplate)
        }

        return binding.root
    }
}
