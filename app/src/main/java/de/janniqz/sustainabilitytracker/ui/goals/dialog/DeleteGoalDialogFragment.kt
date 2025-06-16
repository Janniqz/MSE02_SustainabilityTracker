package de.janniqz.sustainabilitytracker.ui.goals.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import de.janniqz.sustainabilitytracker.R
import de.janniqz.sustainabilitytracker.data.db.AppDatabase
import de.janniqz.sustainabilitytracker.data.model.entity.GoalEntity
import de.janniqz.sustainabilitytracker.databinding.DialogConfirmationBinding
import kotlinx.coroutines.launch

/**
 * Dialog Fragment for Deleting Goals
 */
class DeleteGoalDialogFragment: DialogFragment() {

    private lateinit var dialogContext: Context
    private lateinit var binding: DialogConfirmationBinding

    private var goal: GoalEntity? = null

    companion object {
        const val TAG = "DeleteGoalDialog"
        const val REQUEST_KEY = "deleteGoalRequest"
        const val RESULT_KEY_GOAL_DELETED = "goalDeleted"
    }

    /**
     * Populates the basic Deletion Dialog with Goal related Text
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogContext = requireContext()
        goal = arguments?.getParcelable("goalData")

        binding = DialogConfirmationBinding.inflate(layoutInflater)
        binding.confirmationName.text = getString(R.string.goal_delete)
        binding.confirmationDesc.text = getString(R.string.goal_delete_desc)

        binding.btnConfirm.text = getString(R.string.goal_delete)
        binding.btnConfirm.setOnClickListener { deleteGoal() }
        binding.btnCancel.setOnClickListener { dialog?.dismiss() }

        val builder = AlertDialog.Builder(dialogContext).setView(binding.root)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(R.color.transparent)
        return dialog
    }

    /**
     * Deletes the Goal from the Database
     */
    private fun deleteGoal() {
        val goal = goal!!
        var db = AppDatabase.getInstance(requireContext())

        lifecycleScope.launch {
            db.goal().delete(goal)
            setFragmentResult(REQUEST_KEY, bundleOf(RESULT_KEY_GOAL_DELETED to true))
            dialog?.dismiss()
        }
    }
}