package de.janniqz.sustainabilitytracker.ui.goals.dialog

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import de.janniqz.sustainabilitytracker.R
import de.janniqz.sustainabilitytracker.data.model.TaskCategory
import de.janniqz.sustainabilitytracker.data.model.TimePeriod
import de.janniqz.sustainabilitytracker.data.model.entity.GoalEntity
import kotlinx.coroutines.launch

/**
 * Dialog Fragment for Editing Goals
 */
class EditGoalDialogFragment: GoalDialogBaseFragment() {

    companion object {
        const val TAG = "EditGoalDialog"
        const val REQUEST_KEY = "editGoalRequest"
        const val RESULT_KEY_GOAL_EDITED = "goalEdited"
    }

    private var goal: GoalEntity? = null

    /**
     * Retrieves the existing Goal on Dialog Creation
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        goal = arguments?.getParcelable("goalData")
    }

    /**
     * Populates the Dialog with values from the pre-existing passed Goal
     */
    override fun populateInitialData() {
        goal?.let { goal ->
            binding.inputGoalName.inputField.setText(goal.name)
            binding.inputGoalTarget.inputField.setText(goal.targetAmount.toString())

            when (goal.category) {
                TaskCategory.CO2 -> binding.inputGoalCategory.btnCo2.isChecked = true
                TaskCategory.Water -> binding.inputGoalCategory.btnWater.isChecked = true
                TaskCategory.Waste -> binding.inputGoalCategory.btnWaste.isChecked = true
            }

            when (goal.periodicity) {
                TimePeriod.WEEK -> binding.inputGoalPeriodicity.btnWeek.isChecked = true
                TimePeriod.MONTH -> binding.inputGoalPeriodicity.btnMonth.isChecked = true
                TimePeriod.YEAR -> binding.inputGoalPeriodicity.btnYear.isChecked = true
            }
        }

        binding.btnSubmit.text = getString(R.string.goal_edit)
    }

    /**
     * Updates the Goal in the Database with the current Inputs.
     * Validates Data before processing.
     */
    override fun onSaveClicked() {
        if (!validateData())
            return

        var goalData = goal!!
        goalData.name = binding.inputGoalName.inputField.text.toString()
        goalData.category = getSelectedCategory()!!
        goalData.periodicity = getSelectedPeriodicity()!!
        goalData.targetAmount = binding.inputGoalTarget.inputField.text.toString().toFloat()

        lifecycleScope.launch {
            database.goal().update(goalData)
            setFragmentResult(REQUEST_KEY, bundleOf(RESULT_KEY_GOAL_EDITED to true))
            dialog?.dismiss()
        }
    }
}