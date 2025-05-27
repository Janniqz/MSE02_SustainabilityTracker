package de.janniqz.sustainabilitytracker.ui.tasks.dialog

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
import de.janniqz.sustainabilitytracker.data.model.entity.TaskEntity
import de.janniqz.sustainabilitytracker.databinding.DialogConfirmationBinding
import kotlinx.coroutines.launch


class DeleteTaskDialogFragment: DialogFragment() {

    private lateinit var dialogContext: Context
    private lateinit var binding: DialogConfirmationBinding

    private var task: TaskEntity? = null

    companion object {
        const val TAG = "DeleteTaskDialog"
        const val REQUEST_KEY = "deleteTaskRequest"
        const val RESULT_KEY_TASK_DELETED = "taskDeleted"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogContext = requireContext()
        task = arguments?.getParcelable("taskData")

        binding = DialogConfirmationBinding.inflate(layoutInflater)
        binding.confirmationName.text = getString(R.string.task_delete)
        binding.confirmationDesc.text = getString(R.string.task_delete_desc)

        binding.buttonConfirm.text = getString(R.string.task_delete)
        binding.buttonConfirm.setOnClickListener { deleteTask() }
        binding.buttonCancel.setOnClickListener { dialog?.dismiss() }

        val builder = AlertDialog.Builder(dialogContext).setView(binding.root)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(R.color.transparent)
        return dialog
    }

    private fun deleteTask() {
        val taskData = task!!
        var db = AppDatabase.getInstance(requireContext())

        lifecycleScope.launch {
            db.task().delete(taskData)
            setFragmentResult(REQUEST_KEY, bundleOf(RESULT_KEY_TASK_DELETED to true))
            Toast.makeText(requireContext(), R.string.toast_task_deleted, Toast.LENGTH_SHORT).show()
            dialog?.dismiss()
        }
    }
}