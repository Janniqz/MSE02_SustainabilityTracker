package de.janniqz.sustainabilitytracker.ui.goals.dialog

import AppDatabase
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import de.janniqz.sustainabilitytracker.R
import de.janniqz.sustainabilitytracker.data.model.TaskCategory
import de.janniqz.sustainabilitytracker.data.model.TimePeriod
import de.janniqz.sustainabilitytracker.databinding.DialogGoalBinding

abstract class GoalDialogBaseFragment : DialogFragment() {

    protected lateinit var dialogContext: Context
    protected lateinit var binding: DialogGoalBinding
    protected lateinit var database: AppDatabase

    protected abstract fun populateInitialData()
    protected abstract fun onSaveClicked()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogContext = requireContext()
        database = AppDatabase.getInstance(dialogContext)

        binding = DialogGoalBinding.inflate(layoutInflater)
        binding.inputGoalName.inputContainer.helperText = getString(R.string.goal_dialog_name_help)
        binding.inputGoalTarget.inputContainer.helperText = getString(R.string.goal_dialog_value_help)
        binding.btnCancel.setOnClickListener { dialog?.dismiss() }
        binding.btnSubmit.setOnClickListener { onSaveClicked() }

        populateInitialData()

        val builder = AlertDialog.Builder(dialogContext).setView(binding.root)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(R.color.transparent)
        return dialog
    }

    protected fun validateData(): Boolean {
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

    protected fun getSelectedCategory(): TaskCategory? {
        return when {
            binding.inputGoalCategory.btnCo2.isChecked -> TaskCategory.CO2
            binding.inputGoalCategory.btnWater.isChecked -> TaskCategory.Water
            binding.inputGoalCategory.btnWaste.isChecked -> TaskCategory.Waste
            else -> null
        }
    }

    protected fun getSelectedPeriodicity(): TimePeriod? {
        return when {
            binding.inputGoalPeriodicity.btnWeek.isChecked -> TimePeriod.WEEK
            binding.inputGoalPeriodicity.btnMonth.isChecked -> TimePeriod.MONTH
            binding.inputGoalPeriodicity.btnYear.isChecked -> TimePeriod.YEAR
            else -> null
        }
    }
}