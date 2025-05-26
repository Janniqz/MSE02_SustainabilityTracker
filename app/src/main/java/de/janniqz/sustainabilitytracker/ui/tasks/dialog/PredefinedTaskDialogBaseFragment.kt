package de.janniqz.sustainabilitytracker.ui.tasks.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.InputType
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import de.janniqz.sustainabilitytracker.R
import de.janniqz.sustainabilitytracker.databinding.ComponentTextInputBinding
import de.janniqz.sustainabilitytracker.databinding.DialogPredefinedTaskBinding

abstract class PredefinedTaskDialogBaseFragment : DialogFragment() {

    protected lateinit var context: Context
    protected lateinit var binding: DialogPredefinedTaskBinding
    protected var taskDataFields: MutableList<ComponentTextInputBinding> = mutableListOf<ComponentTextInputBinding>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        context = requireContext()
        binding = DialogPredefinedTaskBinding.inflate(layoutInflater)

        populateBaseDialog()

        val builder = AlertDialog.Builder(context).setView(binding.root)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(R.color.transparent)
        return dialog
    }

    private fun populateBaseDialog() {
        binding.inputTaskName.inputField.setInputType(InputType.TYPE_CLASS_TEXT)
        binding.inputTaskName.inputContainer.hint = context.getString(R.string.task_create_name)
        binding.inputTaskName.inputContainer.helperText = context.getString(R.string.task_create_name_help)

        binding.buttonCancel.setOnClickListener { dialog?.dismiss() }
    }

    protected abstract fun populateDialog()

    protected fun createDataField(helpResource: Int) {
        val inputBinding = ComponentTextInputBinding.inflate(layoutInflater)
        inputBinding.inputField.setInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
        inputBinding.inputContainer.hint = context.getString(R.string.general_value)
        inputBinding.inputContainer.helperText = context.getString(helpResource)

        taskDataFields.add(inputBinding)
        binding.containerRequiredData.addView(inputBinding.root)
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