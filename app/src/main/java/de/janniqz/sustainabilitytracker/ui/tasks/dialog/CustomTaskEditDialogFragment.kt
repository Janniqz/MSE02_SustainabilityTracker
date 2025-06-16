package de.janniqz.sustainabilitytracker.ui.tasks.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import de.janniqz.sustainabilitytracker.data.db.AppDatabase
import de.janniqz.sustainabilitytracker.data.model.TaskCategory
import de.janniqz.sustainabilitytracker.data.model.entity.TaskEntity
import kotlinx.coroutines.launch

/**
 * Dialog for editing Custom Tasks
 */
class CustomTaskEditDialogFragment: CustomTaskDialogFragment() {

    private var task: TaskEntity? = null

    companion object {
        const val TAG = "EditCustomTask"
        const val REQUEST_KEY = "editTaskRequest"
        const val RESULT_KEY_TASK_EDITED = "taskEdited"
    }

    /**
     * Retrieves the existing Task on Dialog Creation
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        task = arguments?.getParcelable("taskData")

        return super.onCreateDialog(savedInstanceState)
    }

    /**
     * Populates Dialog with editing specific values.
     * Applies values from the existing Task.
     */
    override fun populateDialog() {
        super.populateDialog()

        val task = task!!

        binding.inputTaskName.inputField.setText(task.name)
        binding.inputTaskSaving.inputField.setText(task.savings.toString())
        binding.inputTaskCategory.btnCo2.isChecked = task.category == TaskCategory.CO2
        binding.inputTaskCategory.btnWater.isChecked = task.category == TaskCategory.Water
        binding.inputTaskCategory.btnWaste.isChecked = task.category == TaskCategory.Waste
    }

    /**
     * Updates the existing Task.
     * Validates inputs before proceeding.
     */
    override fun onCreateClick() {
        if (!validateData())
            return

        val taskData = task!!
        var selectedCategory: TaskCategory? = null
        when {
            binding.inputTaskCategory.btnCo2.isChecked -> selectedCategory = TaskCategory.CO2
            binding.inputTaskCategory.btnWater.isChecked -> selectedCategory = TaskCategory.Water
            binding.inputTaskCategory.btnWaste.isChecked -> selectedCategory = TaskCategory.Waste
        }

        taskData.name = binding.inputTaskName.inputField.text.toString()
        taskData.savings = binding.inputTaskSaving.inputField.text.toString().toFloat()
        taskData.category = selectedCategory!!

        val db = AppDatabase.getInstance(requireContext())
        lifecycleScope.launch {
            db.task().update(taskData)
            setFragmentResult(REQUEST_KEY, bundleOf(RESULT_KEY_TASK_EDITED to true))
            dialog?.dismiss()
        }
    }
}