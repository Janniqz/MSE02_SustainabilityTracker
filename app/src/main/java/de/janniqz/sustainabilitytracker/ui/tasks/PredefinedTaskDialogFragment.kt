package de.janniqz.sustainabilitytracker.ui.tasks

import AppDatabase
import android.app.Dialog
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import de.janniqz.sustainabilitytracker.R
import de.janniqz.sustainabilitytracker.data.model.TaskTemplate
import de.janniqz.sustainabilitytracker.data.model.TaskType
import de.janniqz.sustainabilitytracker.data.model.entity.TaskEntity
import de.janniqz.sustainabilitytracker.databinding.ComponentTextInputBinding
import de.janniqz.sustainabilitytracker.databinding.DialogPredefinedTaskBinding
import kotlinx.coroutines.launch

class PredefinedTaskDialogFragment : DialogFragment() {

    private lateinit var binding: DialogPredefinedTaskBinding
    private var taskDataFields: MutableList<ComponentTextInputBinding> = mutableListOf<ComponentTextInputBinding>()
    private var taskTemplate: TaskTemplate? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = requireContext()
        binding = DialogPredefinedTaskBinding.inflate(layoutInflater)

        taskTemplate = arguments?.getParcelable("taskTemplate")
        taskTemplate?.let { populateDialog() }

        val builder = AlertDialog.Builder(context).setView(binding.root)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }

    private fun populateDialog() {
        val context = requireContext()
        val template = taskTemplate!!

        binding.taskTemplateName.text = template.name
        binding.taskTemplateDescription.text = template.description

        binding.inputTaskName.inputField.setInputType(InputType.TYPE_CLASS_TEXT)
        binding.inputTaskName.inputContainer.hint = context.getString(R.string.task_create_name)
        binding.inputTaskName.inputContainer.helperText = context.getString(R.string.task_create_name_help)

        template.requiredData?.let { field ->
            val inputBinding = ComponentTextInputBinding.inflate(layoutInflater)
            inputBinding.inputField.setInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
            inputBinding.inputContainer.hint = context.getString(R.string.general_value)
            inputBinding.inputContainer.helperText = context.getString(field)

            taskDataFields.add(inputBinding)
            binding.containerRequiredData.addView(inputBinding.root)
        }

        binding.buttonCancel.setOnClickListener { dialog?.dismiss() }
        binding.buttonCreate.setOnClickListener { createTask() }
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

            description = template.description,
            multiplier = template.multiplier
        )

        val db = AppDatabase.getInstance(requireContext())
        lifecycleScope.launch {
            db.task().insert(task)
            Toast.makeText(requireContext(), R.string.task_created, Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_returnToTaskOverview)
            dialog?.dismiss()
        }
    }

    private fun validateData(): Boolean {
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