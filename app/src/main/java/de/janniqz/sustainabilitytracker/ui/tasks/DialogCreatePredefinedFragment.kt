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
import de.janniqz.sustainabilitytracker.data.model.entity.TaskEntity
import de.janniqz.sustainabilitytracker.databinding.ComponentTextInputBinding
import de.janniqz.sustainabilitytracker.databinding.DialogCreatePredefinedBinding
import kotlinx.coroutines.launch

class DialogCreatePredefinedFragment : DialogFragment() {

    private lateinit var binding: DialogCreatePredefinedBinding
    private var taskDataFields: MutableList<ComponentTextInputBinding> = mutableListOf<ComponentTextInputBinding>()
    private var taskTemplate: TaskTemplate? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = requireContext()
        binding = DialogCreatePredefinedBinding.inflate(layoutInflater)

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
        val template = taskTemplate!!
        var multiplier = template.multiplier
        if (template.requiredData != null) {
            multiplier *= taskDataFields
                .map { it.inputField.text.toString().toFloat() }  // TODO Validation
                .first()  // TODO Adjust if adding multiple data fields
        }

        val task = TaskEntity(
            name = binding.inputTaskName.inputField.text.toString(),
            category = template.category,
            multiplier = multiplier,
            createdAt = System.currentTimeMillis()
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