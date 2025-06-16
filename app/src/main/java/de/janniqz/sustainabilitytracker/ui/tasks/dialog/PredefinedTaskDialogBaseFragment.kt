package de.janniqz.sustainabilitytracker.ui.tasks.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.InputType
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import de.janniqz.sustainabilitytracker.R
import de.janniqz.sustainabilitytracker.databinding.ComponentTextInputPaddingBinding
import de.janniqz.sustainabilitytracker.databinding.DialogPredefinedTaskBinding

/**
 * Base class for Predefined Task Creation / Editing Dialogs
 */
abstract class PredefinedTaskDialogBaseFragment : DialogFragment() {

    protected lateinit var dialogContext: Context
    protected lateinit var binding: DialogPredefinedTaskBinding
    protected var taskDataFields: MutableList<ComponentTextInputPaddingBinding> = mutableListOf<ComponentTextInputPaddingBinding>()

    /**
     * Retrieves references and initializes the Dialog
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogContext = requireContext()
        binding = DialogPredefinedTaskBinding.inflate(layoutInflater)

        populateBaseDialog()
        populateDialog()

        val builder = AlertDialog.Builder(dialogContext).setView(binding.root)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(R.color.transparent)
        return dialog
    }

    /**
     * Populates base Dialog fields
     */
    private fun populateBaseDialog() {
        binding.inputTaskName.inputField.setInputType(InputType.TYPE_CLASS_TEXT)
        binding.inputTaskName.inputContainer.hint = dialogContext.getString(R.string.task_create_name)
        binding.inputTaskName.inputContainer.helperText = dialogContext.getString(R.string.task_create_name_help)

        binding.btnCancel.setOnClickListener { dialog?.dismiss() }
    }

    /**
     * Populates Dialog fields with values depending on the specific implementation
     */
    protected abstract fun populateDialog()

    /**
     * Creates a number data field for the Dialog
     */
    protected fun createDataField(helpResource: Int) {
        val inputBinding = ComponentTextInputPaddingBinding.inflate(layoutInflater)

        inputBinding.inputField.setInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
        inputBinding.inputContainer.hint = dialogContext.getString(R.string.general_value)
        inputBinding.inputContainer.helperText = dialogContext.getString(helpResource)

        taskDataFields.add(inputBinding)
        binding.containerRequiredData.addView(inputBinding.root)
    }

    /**
     * Validates the current Inputs:
     * - Task Name is not empty
     * - Task Data Fields are valid numbers (decimal)
     */
    protected fun validateData(): Boolean {
        var isValid = true

        val taskNameComponent = binding.inputTaskName
        if (taskNameComponent.inputField.text.isNullOrEmpty()) {
            taskNameComponent.inputContainer.error = getString(R.string.general_required)
            isValid = false
        } else {
            taskNameComponent.inputContainer.error = null
        }

        taskDataFields.forEach { field ->
            if (field.inputField.text.isNullOrEmpty()) {
                field.inputContainer.error = getString(R.string.general_required)
                isValid = false
            } else {
                try {
                    field.inputField.text.toString().toFloat()
                } catch (_: NumberFormatException) {
                    field.inputContainer.error = getString(R.string.general_invalid_number)
                    isValid = false
                }
                field.inputContainer.error = null
            }
        }

        return isValid
    }
}