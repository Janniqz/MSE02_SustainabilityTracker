package de.janniqz.sustainabilitytracker.ui.goals

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import de.janniqz.sustainabilitytracker.R
import de.janniqz.sustainabilitytracker.data.model.GoalWithProgress
import de.janniqz.sustainabilitytracker.data.model.TaskCategory
import de.janniqz.sustainabilitytracker.data.model.entity.GoalEntity
import de.janniqz.sustainabilitytracker.databinding.ComponentGoalDisplayBinding

/**
 * List Adapter responsible for displaying Goals
 */
class GoalDisplayAdapter(context: Context, tasks: List<GoalWithProgress>, private val goalEditFunc: (GoalEntity) -> Unit, private val goalDeleteFunc: (GoalEntity) -> Unit) : ArrayAdapter<GoalWithProgress>(context, 0, tasks) {

    /**
     * Initializes a Goal Display.
     * Goal Display allows editing / deleting the associated Goal.
     */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = if (convertView == null) {
            ComponentGoalDisplayBinding.inflate(LayoutInflater.from(context), parent, false)
        } else {
            ComponentGoalDisplayBinding.bind(convertView)
        }

        val goalData = getItem(position)!!
        val goal = goalData.goal
        val progress = goalData.progress
        val progressPercentage = if (goal.targetAmount > 0f) {
            (progress / goal.targetAmount * 100).toInt()
        } else {
            0
        }

        val icon = TaskCategory.getTaskIcon(goal.category)
        val savingsUnit = context.getString(TaskCategory.getSavingsUnit(goal.category))

        binding.goalName.text = goal.name
        binding.goalTarget.text = context.getString(R.string.goal_target, goal.targetAmount, savingsUnit, goal.periodicity.getDisplayName())
        binding.goalProgressText.text = context.getString(R.string.goal_progress, progress, goal.targetAmount, savingsUnit)
        binding.goalProgress.progress = progressPercentage
        binding.goalCategoryIcon.setImageResource(icon)

        binding.btnEdit.setOnClickListener { goalEditFunc(goal) }
        binding.btnDelete.setOnClickListener { goalDeleteFunc(goal) }

        return binding.root
    }
}
