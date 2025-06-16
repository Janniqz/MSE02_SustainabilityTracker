package de.janniqz.sustainabilitytracker.ui.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import de.janniqz.sustainabilitytracker.data.model.TaskCategory
import de.janniqz.sustainabilitytracker.data.model.TaskTemplate
import de.janniqz.sustainabilitytracker.data.model.TaskTemplates
import de.janniqz.sustainabilitytracker.databinding.FragmentCreatePredefinedTaskBinding
import de.janniqz.sustainabilitytracker.ui.tasks.dialog.PredefinedTaskDialogCreateFragment

/**
 * Fragment displaying the existing Task Templates and facilitating the creation of new Predefined Tasks
 */
class CreatePredefinedTaskFragment : Fragment() {

    private lateinit var binding: FragmentCreatePredefinedTaskBinding
    private lateinit var templateListAdapter: TaskTemplateAdapter

    /**
     * Base Fragment Initialization
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCreatePredefinedTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Sets up Event Listeners and shows the initial list of Task Templates
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the template list adapter with an empty list and a click listener
        templateListAdapter = TaskTemplateAdapter(requireContext(), mutableListOf(), ::showTaskDialog)
        binding.templateList.adapter = templateListAdapter
        binding.categorySelection.btnCo2.isChecked = true

        // Set initial category and load templates
        selectCategory(TaskCategory.CO2)

        // Set up category selection listeners
        binding.categorySelection.btnCo2.setOnClickListener { selectCategory(TaskCategory.CO2) }
        binding.categorySelection.btnWater.setOnClickListener { selectCategory(TaskCategory.Water) }
        binding.categorySelection.btnWaste.setOnClickListener { selectCategory(TaskCategory.Waste) }
    }

    /**
     * Updates the list of Task Templates with the passed category
     */
    private fun selectCategory(category: TaskCategory) {
        val taskTemplates = TaskTemplates.getTemplatesByCategory(category)
        templateListAdapter.clear()
        templateListAdapter.addAll(taskTemplates)
        templateListAdapter.notifyDataSetChanged()
    }

    /**
     * Opens the Predefined Task Creation Dialog
     */
    private fun showTaskDialog(taskTemplate: TaskTemplate) {
        val dialog = PredefinedTaskDialogCreateFragment()
        dialog.arguments = bundleOf("taskTemplate" to taskTemplate)
        dialog.show(getParentFragmentManager(), PredefinedTaskDialogCreateFragment.TAG)
    }
}
