package de.janniqz.sustainabilitytracker.ui.goals

import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import de.janniqz.sustainabilitytracker.R
import de.janniqz.sustainabilitytracker.data.db.AppDatabase
import de.janniqz.sustainabilitytracker.data.model.GoalWithProgress
import de.janniqz.sustainabilitytracker.data.model.entity.GoalEntity
import de.janniqz.sustainabilitytracker.databinding.FragmentGoalsBinding
import de.janniqz.sustainabilitytracker.tools.DateHelper
import de.janniqz.sustainabilitytracker.ui.goals.dialog.CreateGoalDialogFragment
import de.janniqz.sustainabilitytracker.ui.goals.dialog.DeleteGoalDialogFragment
import de.janniqz.sustainabilitytracker.ui.goals.dialog.EditGoalDialogFragment
import kotlinx.coroutines.launch

/**
 * Fragment displaying the currently existing Goals.
 * Allows Creation of new Goals.
 */
class GoalsFragment : Fragment() {

    private lateinit var binding: FragmentGoalsBinding
    private lateinit var goalListAdapter: GoalDisplayAdapter
    private lateinit var database: AppDatabase

    private var currentGoals: MutableList<GoalWithProgress> = mutableListOf()

    /**
     * Retrieves Binding and Database references on Fragment Creation
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentGoalsBinding.inflate(inflater, container, false)
        database = AppDatabase.getInstance(requireContext())
        return binding.root
    }

    /**
     * Initializes UI Events and List Adapter.
     * Loads initial list of Goals.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabNewGoal.setOnClickListener { onCreateGoal() }

        // Initialize the goal list adapter with an empty list and the action handlers
        goalListAdapter = GoalDisplayAdapter(
            requireContext(),
            currentGoals,
            ::onEditGoal,
            ::onDeleteGoal
        )
        binding.goalList.adapter = goalListAdapter

        createListeners()

        // Load goals from the database
        loadGoals()
    }

    /**
     * Sets up Fragment Result Listeners for Goal Creation, Edit, and Deletion
     */
    private fun createListeners() {
        // Add listener for Goal Creations
        setFragmentResultListener(CreateGoalDialogFragment.REQUEST_KEY) { requestKey, bundle ->
            val goalCreated = bundle.getBoolean(CreateGoalDialogFragment.RESULT_KEY_GOAL_CREATED)
            if (goalCreated)
                onGoalCreated()
        }

        // Add listener for Goal Deletions
        setFragmentResultListener(DeleteGoalDialogFragment.REQUEST_KEY) { requestKey, bundle ->
            val goalDeleted = bundle.getBoolean(DeleteGoalDialogFragment.RESULT_KEY_GOAL_DELETED)
            if (goalDeleted)
                onGoalDeleted()
        }

        // Add listener for Goal Edits
        setFragmentResultListener(EditGoalDialogFragment.REQUEST_KEY) { requestKey, bundle ->
            val goalEdited = bundle.getBoolean(EditGoalDialogFragment.RESULT_KEY_GOAL_EDITED)
            if (goalEdited)
                onGoalEdited()
        }
    }

    /**
     * Refreshes the list of currently displayed Goals
     */
    fun loadGoals() {
        lifecycleScope.launch {
            val goals = database.goal().getAll()
            val goalsWithProgress = mutableListOf<GoalWithProgress>()

            for (goal in goals) {
                val (currentPeriodStart, currentPeriodEnd) = DateHelper.getCurrentPeriodicityRange(goal.periodicity, Calendar.getInstance())
                var progress = 0f

                // Get all tasks that match the goal's category
                val relevantTasks = database.task().getAllByCategory(goal.category)
                for (task in relevantTasks) {
                    // Get completions for this task within the goal's current period
                    val completionsInPeriod = database.taskCompletion().getCountByTaskBetweenDates(task.id, currentPeriodStart, currentPeriodEnd)
                    progress += completionsInPeriod * task.savings
                }

                goalsWithProgress.add(GoalWithProgress(goal, progress))
            }


            currentGoals.clear()
            currentGoals.addAll(goalsWithProgress)
            goalListAdapter.notifyDataSetChanged()
        }
    }

    /**
     * Creates the Goal Creation Dialog
     */
    private fun onCreateGoal() {
        val dialog = CreateGoalDialogFragment()
        dialog.show(getParentFragmentManager(), CreateGoalDialogFragment.TAG)
    }

    /**
     * Creates the Goal Editing Dialog
     */
    private fun onEditGoal(goal: GoalEntity) {
        val dialog = EditGoalDialogFragment()
        dialog.arguments = bundleOf("goalData" to goal)
        dialog.show(getParentFragmentManager(), EditGoalDialogFragment.TAG)
    }

    /**
     * Creates the Goal Deletion Dialog
     */
    private fun onDeleteGoal(goal: GoalEntity) {
        val dialog = DeleteGoalDialogFragment()
        dialog.arguments = bundleOf("goalData" to goal)
        dialog.show(getParentFragmentManager(), DeleteGoalDialogFragment.TAG)
    }

    /**
     * Creates a Toast after Goal Creation and refreshes the Goal List
     */
    private fun onGoalCreated() {
        Toast.makeText(requireContext(), R.string.toast_goal_created, Toast.LENGTH_SHORT).show()
        loadGoals()
    }

    /**
     * Creates a Toast after Goal Editing and refreshes the Goal List
     */
    private fun onGoalEdited() {
        Toast.makeText(requireContext(), R.string.toast_goal_updated, Toast.LENGTH_SHORT).show()
        loadGoals()
    }

    /**
     * Creates a Toast after Goal Deletion and refreshes the Goal List
     */
    private fun onGoalDeleted() {
        Toast.makeText(requireContext(), R.string.toast_goal_deleted, Toast.LENGTH_SHORT).show()
        loadGoals()
    }

}