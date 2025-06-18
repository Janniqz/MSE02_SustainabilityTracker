package de.janniqz.sustainabilitytracker.ui.goals.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import de.janniqz.sustainabilitytracker.R
import de.janniqz.sustainabilitytracker.data.db.AppDatabase
import de.janniqz.sustainabilitytracker.data.model.TaskCategory
import de.janniqz.sustainabilitytracker.data.model.TimePeriod
import de.janniqz.sustainabilitytracker.databinding.DialogGoalBinding

/**
 * Base class for Goal Creating / Editing Dialogs
 */
abstract class GoalDialogBaseFragment : DialogFragment() {

    protected lateinit var dialogContext: Context
    protected lateinit var binding: DialogGoalBinding
    protected lateinit var database: AppDatabase

    /**
     * Populates the Dialog with some basic fields depending on the concrete implementation of the Dialog
     */
    protected abstract fun populateInitialData()
    protected abstract fun onSaveClicked()

    /**
     * Dialog base setup.
     */
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

    /**
     * Validates the current Inputs:
     * - Goal Name is not empty
     * - Goal Category is not empty
     * - Goal Periodicity is not empty
     * - Goal Target Amount is a valid number (decimal)
     */
    protected fun validateData(): Boolean {
        var isValid = true

        // Validate Goal Name
        val goalNameComponent = binding.inputGoalName
        if (goalNameComponent.inputField.text.isNullOrEmpty()) {
            goalNameComponent.inputContainer.error = getString(R.string.general_required)
            isValid = false
        } else {
            goalNameComponent.inputContainer.error = null
        }

        // Validate Goal Category
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

        // Validate Goal Periodicity
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

        // Validate Goal Target Amount
        val valueInput = binding.inputGoalTarget
        if (valueInput.inputField.text.isNullOrEmpty()) {
            valueInput.inputContainer.error = getString(R.string.general_required)
            isValid = false
        } else {
            try {
                if (valueInput.inputField.text.toString().toFloat() <= 0) {
                    valueInput.inputContainer.error = getString(R.string.general_invalid_number)
                    isValid = false
                }
                else
                    valueInput.inputContainer.error = null
            } catch (_: NumberFormatException) {
                valueInput.inputContainer.error = getString(R.string.general_invalid_number)
                isValid = false
            }
        }

        return isValid
    }

    /**
     * Returns the currently selected TaskCategory
     */
    protected fun getSelectedCategory(): TaskCategory? {
        return when {
            binding.inputGoalCategory.btnCo2.isChecked -> TaskCategory.CO2
            binding.inputGoalCategory.btnWater.isChecked -> TaskCategory.Water
            binding.inputGoalCategory.btnWaste.isChecked -> TaskCategory.Waste
            else -> null
        }
    }

    /**
     * Returns the currently selected TimePeriod
     */
    protected fun getSelectedPeriodicity(): TimePeriod? {
        return when {
            binding.inputGoalPeriodicity.btnWeek.isChecked -> TimePeriod.WEEK
            binding.inputGoalPeriodicity.btnMonth.isChecked -> TimePeriod.MONTH
            binding.inputGoalPeriodicity.btnYear.isChecked -> TimePeriod.YEAR
            else -> null
        }
    }
}