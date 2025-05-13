package de.janniqz.sustainabilitytracker.ui.tasks

import android.app.Dialog
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import de.janniqz.sustainabilitytracker.R
import de.janniqz.sustainabilitytracker.data.model.TaskTemplate
import de.janniqz.sustainabilitytracker.databinding.ComponentTextInputBinding
import de.janniqz.sustainabilitytracker.databinding.DialogCreatePredefinedBinding

class DialogCreatePredefinedFragment : DialogFragment() {

    private var taskTemplate: TaskTemplate? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = requireContext()
        val binding = DialogCreatePredefinedBinding.inflate(layoutInflater)

        taskTemplate = arguments?.getParcelable("taskTemplate")
        taskTemplate?.let {  template ->

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
                binding.containerRequiredData.addView(inputBinding.root)
            }

            binding.buttonCancel.setOnClickListener { dialog?.cancel() }
            binding.buttonCreate.setOnClickListener {
                // TODO
                dialog?.cancel()
            }
        }

        val builder = AlertDialog.Builder(context).setView(binding.root)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }
}