package de.janniqz.sustainabilitytracker.ui.tasks.dialog

import AppDatabase
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import de.janniqz.sustainabilitytracker.R
import de.janniqz.sustainabilitytracker.data.model.TaskTemplate
import de.janniqz.sustainabilitytracker.data.model.TaskType
import de.janniqz.sustainabilitytracker.data.model.entity.TaskEntity
import kotlinx.coroutines.launch

class PredefinedTaskDialogCreateFragment : PredefinedTaskDialogBaseFragment() {

    private var taskTemplate: TaskTemplate? = null

    override fun populateDialog() {
        taskTemplate = arguments?.getParcelable("taskTemplate")
        taskTemplate?.let { template ->
            binding.taskTemplateName.text = template.name
            binding.taskTemplateDescription.text = template.description

            binding.buttonCreate.setText(R.string.task_create)
            binding.buttonCreate.setOnClickListener { createTask() }

            template.requiredData?.let { field -> createDataField(field) }
        }
    }

    private fun createTask() {
        if (!validateData())
            return

        val template = taskTemplate!!
        var savings = template.multiplier
        if (template.requiredData != null) {
            savings *= taskDataFields
                .map { it.inputField.text.toString().toFloat() }
                .first()  // Only one requiredData field is supported
        }

        val task = TaskEntity(
            name = binding.inputTaskName.inputField.text.toString(),
            type = TaskType.Predefined,
            category = template.category,
            savings = savings,
            createdAt = System.currentTimeMillis(),

            templateId = template.id
        )

        val db = AppDatabase.getInstance(requireContext())
        lifecycleScope.launch {
            db.task().insert(task)
            Toast.makeText(requireContext(), R.string.task_created, Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_returnToTaskOverview)
            dialog?.dismiss()
        }
    }

}