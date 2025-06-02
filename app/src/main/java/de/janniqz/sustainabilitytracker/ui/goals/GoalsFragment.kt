package de.janniqz.sustainabilitytracker.ui.goals

import AppDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import de.janniqz.sustainabilitytracker.R
import de.janniqz.sustainabilitytracker.data.model.GoalWithProgress
import de.janniqz.sustainabilitytracker.data.model.entity.GoalEntity
import de.janniqz.sustainabilitytracker.databinding.FragmentGoalsBinding

class GoalsFragment : Fragment() {

    private lateinit var binding: FragmentGoalsBinding
    private lateinit var goalListAdapter: GoalDisplayAdapter
    private lateinit var database: AppDatabase

    private var currentGoals: MutableList<GoalWithProgress> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentGoalsBinding.inflate(inflater, container, false)
        database = AppDatabase.getInstance(requireContext())
        return binding.root
    }

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

    private fun createListeners() {
        // TODO
    }

    private fun loadGoals() {
        // TODO
    }

    private fun onCreateGoal() {
        // TODO
    }

    private fun onEditGoal(goal: GoalEntity) {
        // TODO
    }

    private fun onDeleteGoal(goal: GoalEntity) {
        // TODO
    }

    private fun onGoalCreated() {
        Toast.makeText(requireContext(), R.string.toast_goal_created, Toast.LENGTH_SHORT).show()
        loadGoals()
    }

    private fun onGoalEdited() {
        Toast.makeText(requireContext(), R.string.toast_goal_updated, Toast.LENGTH_SHORT).show()
        loadGoals()
    }

    private fun onGoalDeleted() {
        Toast.makeText(requireContext(), R.string.toast_goal_deleted, Toast.LENGTH_SHORT).show()
        loadGoals()
    }

}