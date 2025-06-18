package de.janniqz.sustainabilitytracker.ui.tasks.dialog

import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import de.janniqz.sustainabilitytracker.R
import de.janniqz.sustainabilitytracker.data.db.AppDatabase
import de.janniqz.sustainabilitytracker.data.model.TaskTemplate
import de.janniqz.sustainabilitytracker.data.model.TaskType
import de.janniqz.sustainabilitytracker.data.model.entity.TaskEntity
import kotlinx.coroutines.launch

/**
 * Dialog for creating Predefined Tasks
 */
class PredefinedTaskDialogCreateFragment : PredefinedTaskDialogBaseFragment() {

    private var taskTemplate: TaskTemplate? = null

    companion object {
        const val TAG = "CreatePredefinedTask"
    }

    /**
     * Retrieves the selected Task Template and populates the Dialog Fields
     */
    override fun populateDialog() {
        taskTemplate = arguments?.getParcelable("taskTemplate")
        taskTemplate?.let { template ->
            binding.taskTemplateName.text = template.name
            binding.taskTemplateDescription.text = template.description

            binding.btnSubmit.setText(R.string.task_create)
            binding.btnSubmit.setOnClickListener { createTask() }

            template.requiredData?.let { field -> createDataField(field) }
        }
    }

    /**
     * Saves the Task to the Database with the current Inputs.
     * Validates Data before processing.
     */
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
            Toast.makeText(requireContext(), R.string.toast_task_created, Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_returnToTaskOverview_predefined)
            dialog?.dismiss()
        }
    }

}