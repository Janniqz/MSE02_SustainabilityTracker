package de.janniqz.sustainabilitytracker.ui.tasks.dialog

import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import de.janniqz.sustainabilitytracker.R
import de.janniqz.sustainabilitytracker.data.db.AppDatabase
import de.janniqz.sustainabilitytracker.data.model.TaskTemplate
import de.janniqz.sustainabilitytracker.data.model.TaskTemplates
import de.janniqz.sustainabilitytracker.data.model.entity.TaskEntity
import kotlinx.coroutines.launch

/**
 * Dialog for editing Predefined Tasks
 */
class PredefinedTaskDialogEditFragment : PredefinedTaskDialogBaseFragment() {

    private var task: TaskEntity? = null
    private lateinit var template: TaskTemplate

    companion object {
        const val TAG = "EditPredefinedTask"
        const val REQUEST_KEY = "editTaskRequest"
        const val RESULT_KEY_TASK_EDITED = "taskEdited"
    }

    /**
     * Retrieves the passed Task and used Task Template and populates dialog fields
     */
    override fun populateDialog() {
        task = arguments?.getParcelable("taskData")
        task?.let { taskData ->
            template = TaskTemplates.getTemplateById(taskData.templateId!!)!!

            binding.taskTemplateName.text = taskData.name
            binding.taskTemplateDescription.text = template.description

            binding.inputTaskName.inputField.setText(taskData.name)

            binding.btnSubmit.setText(R.string.task_edit)
            binding.btnSubmit.setOnClickListener { editTask() }

            template.requiredData?.let { field -> createDataField(field) }
        }
    }

    /**
     * Updates the Predefined Task in the Database.
     * Validates inputs before proceeding.
     */
    private fun editTask() {
        if (!validateData())
            return

        val taskData = task!!
        val template = TaskTemplates.getTemplateById(taskData.templateId!!)!!
        var savings = template.multiplier
        if (template.requiredData != null) {
            savings *= taskDataFields
                .map { it.inputField.text.toString().toFloat() }
                .first()  // Only one requiredData field is supported
        }

        taskData.name = binding.inputTaskName.inputField.text.toString()
        taskData.savings = savings

        val db = AppDatabase.getInstance(requireContext())
        lifecycleScope.launch {
            db.task().update(taskData)
            setFragmentResult(REQUEST_KEY, bundleOf(RESULT_KEY_TASK_EDITED to true))
            dialog?.dismiss()
        }
    }

}