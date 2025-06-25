package de.janniqz.sustainabilitytracker.ui.goals.dialog

import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import de.janniqz.sustainabilitytracker.R
import de.janniqz.sustainabilitytracker.data.model.entity.GoalEntity
import kotlinx.coroutines.launch

/**
 * Dialog Fragment for Creating Goals
 */
class CreateGoalDialogFragment: GoalDialogBaseFragment() {

    companion object {
        const val TAG = "CreateGoalDialog"
        const val REQUEST_KEY = "createGoalRequest"
        const val RESULT_KEY_GOAL_CREATED = "goalCreated"
    }

    /**
     * Populates the Dialog with base values for Goal Creation
     */
    override fun populateInitialData() {
        binding.dialogGoalTitle.setText(R.string.goal_create)

        binding.inputGoalCategory.btnCo2.isChecked = true
        binding.inputGoalPeriodicity.btnMonth.isChecked = true

        binding.btnSubmit.text = getString(R.string.goal_create)
    }

    /**
     * Saves the Goal to the Database with the current Inputs.
     * Validates Data before processing.
     */
    override fun onSaveClicked() {
        if (!validateData())
            return

        val goal = GoalEntity(
            name = binding.inputGoalName.inputField.text.toString(),
            category = getSelectedCategory()!!,
            periodicity = getSelectedPeriodicity()!!,
            targetAmount = binding.inputGoalTarget.inputField.text.toString().toFloat()
        )

        lifecycleScope.launch {
            database.goal().insert(goal)
            setFragmentResult(REQUEST_KEY, bundleOf(RESULT_KEY_GOAL_CREATED to true))
            dialog?.dismiss()
        }
    }
}