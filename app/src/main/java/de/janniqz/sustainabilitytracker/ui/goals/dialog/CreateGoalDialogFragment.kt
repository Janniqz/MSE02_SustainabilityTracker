package de.janniqz.sustainabilitytracker.ui.goals.dialog

import AppDatabase
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import de.janniqz.sustainabilitytracker.R
import de.janniqz.sustainabilitytracker.data.model.TaskCategory
import de.janniqz.sustainabilitytracker.data.model.TimePeriod
import de.janniqz.sustainabilitytracker.data.model.entity.GoalEntity
import de.janniqz.sustainabilitytracker.databinding.DialogGoalBinding
import kotlinx.coroutines.launch

class CreateGoalDialogFragment: DialogFragment() {

    private lateinit var dialogContext: Context
    private lateinit var binding: DialogGoalBinding

    companion object {
        const val TAG = "CreateGoalDialog"
        const val REQUEST_KEY = "createGoalRequest"
        const val RESULT_KEY_GOAL_CREATED = "goalCreated"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogContext = requireContext()

        binding = DialogGoalBinding.inflate(layoutInflater)
        binding.inputGoalCategory.btnCo2.isChecked = true
        binding.inputGoalPeriodicity.btnMonth.isChecked = true

        binding.inputGoalName.inputContainer.helperText = getString(R.string.goal_dialog_name_help)
        binding.inputGoalTarget.inputContainer.helperText = getString(R.string.goal_dialog_value_help)

        binding.btnSubmit.text = getString(R.string.goal_create)
        binding.btnSubmit.setOnClickListener { onCreateClick() }
        binding.btnCancel.setOnClickListener { dialog?.dismiss() }

        val builder = AlertDialog.Builder(dialogContext).setView(binding.root)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(R.color.transparent)
        return dialog
    }

    private fun onCreateClick() {
        if (!validateData())
            return

        var goalCategory: TaskCategory? = null
        when {
            binding.inputGoalCategory.btnCo2.isChecked -> goalCategory = TaskCategory.CO2
            binding.inputGoalCategory.btnWater.isChecked -> goalCategory = TaskCategory.Water
            binding.inputGoalCategory.btnWaste.isChecked -> goalCategory = TaskCategory.Waste
        }

        var goalPeriodicity: TimePeriod? = null
        when {
            binding.inputGoalPeriodicity.btnWeek.isChecked -> goalPeriodicity = TimePeriod.WEEK
            binding.inputGoalPeriodicity.btnMonth.isChecked -> goalPeriodicity = TimePeriod.MONTH
            binding.inputGoalPeriodicity.btnYear.isChecked -> goalPeriodicity = TimePeriod.YEAR
        }

        val goal = GoalEntity(
            name = binding.inputGoalName.inputField.text.toString(),
            category = goalCategory!!,
            periodicity = goalPeriodicity!!,
            targetAmount = binding.inputGoalTarget.inputField.text.toString().toFloat()
        )

        val db = AppDatabase.getInstance(requireContext())
        lifecycleScope.launch {
            db.goal().insert(goal)
            setFragmentResult(REQUEST_KEY, bundleOf(RESULT_KEY_GOAL_CREATED to true))
            dialog?.dismiss()
        }
    }

    private fun validateData(): Boolean {
        var isValid = true

        val goalNameComponent = binding.inputGoalName
        if (goalNameComponent.inputField.text.isNullOrEmpty()) {
            goalNameComponent.inputContainer.error = getString(R.string.general_required)
            isValid = false
        } else {
            goalNameComponent.inputContainer.error = null
        }

        var goalCategory: TaskCategory? = null
        when {
            binding.inputGoalCategory.btnCo2.isChecked -> goalCategory = TaskCategory.CO2
            binding.inputGoalCategory.btnWater.isChecked -> goalCategory = TaskCategory.Water
            binding.inputGoalCategory.btnWaste.isChecked -> goalCategory = TaskCategory.Waste
        }
        if (goalCategory == null) {
            Toast.makeText(dialogContext, R.string.general_category_required, Toast.LENGTH_SHORT).show()
            isValid = false
        }

        var goalPeriodicity: TimePeriod? = null
        when {
            binding.inputGoalPeriodicity.btnWeek.isChecked -> goalPeriodicity = TimePeriod.WEEK
            binding.inputGoalPeriodicity.btnMonth.isChecked -> goalPeriodicity = TimePeriod.MONTH
            binding.inputGoalPeriodicity.btnYear.isChecked -> goalPeriodicity = TimePeriod.YEAR
        }
        if (goalPeriodicity == null) {
            Toast.makeText(dialogContext, R.string.general_periodicity_required, Toast.LENGTH_SHORT).show()
            isValid = false
        }

        val valueInput = binding.inputGoalTarget
        if (valueInput.inputField.text.isNullOrEmpty()) {
            valueInput.inputContainer.error = getString(R.string.general_required)
            isValid = false
        } else {
            try {
                valueInput.inputField.text.toString().toFloat()
            } catch (_: NumberFormatException) {
                valueInput.inputContainer.error = getString(R.string.general_invalid_number)
                isValid = false
            }
            valueInput.inputContainer.error = null
        }

        return isValid
    }
}