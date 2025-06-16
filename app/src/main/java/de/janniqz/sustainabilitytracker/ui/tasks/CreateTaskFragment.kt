package de.janniqz.sustainabilitytracker.ui.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import de.janniqz.sustainabilitytracker.R
import de.janniqz.sustainabilitytracker.databinding.FragmentCreateTaskBinding
import de.janniqz.sustainabilitytracker.ui.tasks.dialog.CustomTaskDialogFragment

/**
 * Fragment for choosing between creating a new Predefined or Custom Task
 */
class CreateTaskFragment : Fragment() {

    private lateinit var binding: FragmentCreateTaskBinding

    /**
     * Base Fragment Initialization
     * Sets up Buttons
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCreateTaskBinding.inflate(inflater, container, false)

        setupButtons()

        return binding.root
    }

    /**
     * Populates Predefined / Custom Buttons with values and sets up their Event Listeners
     */
    private fun setupButtons() {
        binding.btnPredefined.btnTitle.setText(R.string.task_type_predefined_title)
        binding.btnPredefined.btnDesc.setText(R.string.task_type_predefined_desc)
        binding.btnPredefined.btnIcon.setImageResource(R.drawable.ic_task_predefined)
        binding.btnPredefined.btnRoot.setOnClickListener { _ ->
            findNavController().navigate(R.id.action_createPredefinedTask)
        }

        binding.btnCustom.btnTitle.setText(R.string.task_type_custom_title)
        binding.btnCustom.btnDesc.setText(R.string.task_type_custom_desc)
        binding.btnCustom.btnIcon.setImageResource(R.drawable.ic_task_custom)
        binding.btnCustom.btnRoot.setOnClickListener {
            onCustomTaskClick()
        }
    }

    /**
     * Opens the Custom Task Creation Dialog
     */
    private fun onCustomTaskClick() {
        val dialog = CustomTaskDialogFragment()
        dialog.show(getParentFragmentManager(), CustomTaskDialogFragment.TAG)
    }
}