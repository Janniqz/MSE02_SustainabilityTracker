package de.janniqz.sustainabilitytracker.ui.tasks.dialog

import AppDatabase
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import de.janniqz.sustainabilitytracker.R
import de.janniqz.sustainabilitytracker.data.model.TaskCategory
import de.janniqz.sustainabilitytracker.data.model.TaskType
import de.janniqz.sustainabilitytracker.data.model.entity.TaskEntity
import de.janniqz.sustainabilitytracker.databinding.DialogCustomTaskBinding
import kotlinx.coroutines.launch

open class CustomTaskDialogFragment: DialogFragment() {

    protected lateinit var dialogContext: Context
    protected lateinit var binding: DialogCustomTaskBinding

    companion object {
        const val TAG = "CustomTask"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogContext = requireContext()
        binding = DialogCustomTaskBinding.inflate(layoutInflater)

        populateDialog()

        val builder = AlertDialog.Builder(dialogContext).setView(binding.root)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(R.color.transparent)
        return dialog
    }

    protected open fun populateDialog() {
        binding.inputTaskName.inputField.setInputType(InputType.TYPE_CLASS_TEXT)
        binding.inputTaskName.inputContainer.hint = dialogContext.getString(R.string.task_create_name)
        binding.inputTaskName.inputContainer.helperText = dialogContext.getString(R.string.task_create_name)

        binding.inputTaskSaving.inputField.setInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
        binding.inputTaskSaving.inputContainer.hint = dialogContext.getString(R.string.general_value)
        binding.inputTaskSaving.inputContainer.helperText = dialogContext.getString(R.string.task_create_savings)

        binding.buttonCreate.setOnClickListener { onCreateClick() }
        binding.buttonCancel.setOnClickListener { dialog?.dismiss() }
    }

    protected open fun onCreateClick() {
        if (!validateData())
            return

        var taskCategory: TaskCategory? = null
        when {
            binding.inputTaskCategory.btnCo2.isChecked -> taskCategory = TaskCategory.CO2
            binding.inputTaskCategory.btnWater.isChecked -> taskCategory = TaskCategory.Water
            binding.inputTaskCategory.btnWaste.isChecked -> taskCategory = TaskCategory.Waste
        }

        val task = TaskEntity(
            name = binding.inputTaskName.inputField.text.toString(),
            type = TaskType.Custom,
            category = taskCategory!!,
            savings = binding.inputTaskSaving.inputField.text.toString().toFloat(),
            createdAt = System.currentTimeMillis()
        )

        val db = AppDatabase.getInstance(requireContext())
        lifecycleScope.launch {
            db.task().insert(task)
            Toast.makeText(requireContext(), R.string.toast_task_created, Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_returnToTaskOverview_custom)
            dialog?.dismiss()
        }
    }

    protected fun validateData(): Boolean {
        var isValid = true

        val taskNameComponent = binding.inputTaskName
        if (taskNameComponent.inputField.text.isNullOrEmpty()) {
            taskNameComponent.inputContainer.error = getString(R.string.general_required)
            isValid = false
        } else {
            taskNameComponent.inputContainer.error = null
        }

        var taskCategory: TaskCategory? = null
        when {
            binding.inputTaskCategory.btnCo2.isChecked -> taskCategory = TaskCategory.CO2
            binding.inputTaskCategory.btnWater.isChecked -> taskCategory = TaskCategory.Water
            binding.inputTaskCategory.btnWaste.isChecked -> taskCategory = TaskCategory.Waste
        }
        if (taskCategory == null) {
            Toast.makeText(dialogContext, R.string.task_create_category_required, Toast.LENGTH_SHORT).show()
            isValid = false
        }

        val savingsInput = binding.inputTaskSaving
        if (savingsInput.inputField.text.isNullOrEmpty()) {
            savingsInput.inputContainer.error = getString(R.string.general_required)
            isValid = false
        } else {
            try {
                savingsInput.inputField.text.toString().toFloat()
            } catch (_: NumberFormatException) {
                savingsInput.inputContainer.error = getString(R.string.general_invalid_number)
                isValid = false
            }
            savingsInput.inputContainer.error = null
        }

        return isValid
    }
}