package de.janniqz.sustainabilitytracker.ui.tasks

import AppDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import de.janniqz.sustainabilitytracker.R
import de.janniqz.sustainabilitytracker.data.model.TaskType
import de.janniqz.sustainabilitytracker.data.model.TaskWithCompletions
import de.janniqz.sustainabilitytracker.data.model.entity.TaskCompletionEntity
import de.janniqz.sustainabilitytracker.data.model.entity.TaskEntity
import de.janniqz.sustainabilitytracker.databinding.FragmentTasksBinding
import de.janniqz.sustainabilitytracker.ui.tasks.dialog.CustomTaskEditDialogFragment
import de.janniqz.sustainabilitytracker.ui.tasks.dialog.DeleteTaskDialogFragment
import de.janniqz.sustainabilitytracker.ui.tasks.dialog.PredefinedTaskDialogEditFragment
import kotlinx.coroutines.launch

class TasksFragment : Fragment() {

    private lateinit var binding: FragmentTasksBinding
    private lateinit var taskListAdapter: TaskDisplayAdapter
    private lateinit var database: AppDatabase

    private var currentTasks: MutableList<TaskWithCompletions> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTasksBinding.inflate(inflater, container, false)
        database = AppDatabase.getInstance(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabNewTask.setOnClickListener {
            findNavController().navigate(R.id.action_createTask)
        }

        // Initialize the task list adapter with an empty list and the action handlers
        taskListAdapter = TaskDisplayAdapter(requireContext(), currentTasks, ::onEditTask, ::onCompleteTask, ::onDeleteTask)
        binding.taskList.adapter = taskListAdapter

        createListeners()

        // Load tasks from the database
        loadTasks()
    }

    private fun createListeners() {
        // TODO Combine these

        // Add listener for Predefined Task Edits
        setFragmentResultListener(PredefinedTaskDialogEditFragment.REQUEST_KEY) { requestKey, bundle ->
            val taskEdited = bundle.getBoolean(PredefinedTaskDialogEditFragment.RESULT_KEY_TASK_EDITED)
            if (taskEdited)
                onTaskEdited()
        }

        // Add listener for Custom Task Edits
        setFragmentResultListener(CustomTaskEditDialogFragment.REQUEST_KEY) { requestKey, bundle ->
            val taskEdited = bundle.getBoolean(CustomTaskEditDialogFragment.RESULT_KEY_TASK_EDITED)
            if (taskEdited)
                onTaskEdited()
        }

        // Add listener for Task Deletions
        setFragmentResultListener(DeleteTaskDialogFragment.REQUEST_KEY) { requestKey, bundle ->
            val taskDeleted = bundle.getBoolean(DeleteTaskDialogFragment.RESULT_KEY_TASK_DELETED)
            if (taskDeleted)
                onTaskDeleted()
        }
    }

    private fun loadTasks() {
        lifecycleScope.launch {
            val tasks = database.task().getAll()
            val tasksWithCompletions = tasks.map { task -> TaskWithCompletions(task, database.taskCompletion().getCountByTask(task.id)) }

            currentTasks.clear()
            currentTasks.addAll(tasksWithCompletions)
            taskListAdapter.notifyDataSetChanged()
        }
    }

    private fun onEditTask(task: TaskEntity) {
        if (task.type == TaskType.Predefined) {
            val dialog = PredefinedTaskDialogEditFragment()
            dialog.arguments = bundleOf("taskData" to task)
            dialog.show(getParentFragmentManager(), PredefinedTaskDialogEditFragment.TAG)
        } else {
            val dialog = CustomTaskEditDialogFragment()
            dialog.arguments = bundleOf("taskData" to task)
            dialog.show(getParentFragmentManager(), CustomTaskEditDialogFragment.TAG)
        }
    }

    private fun onCompleteTask(task: TaskEntity) {
        val completion = TaskCompletionEntity(
            taskId = task.id,
            completionTime = System.currentTimeMillis()
        )

        lifecycleScope.launch {
            database.taskCompletion().insert(completion)
            Toast.makeText(requireContext(), R.string.toast_task_completed, Toast.LENGTH_SHORT).show()
            loadTasks()
        }
    }

    private fun onDeleteTask(task: TaskEntity) {
        val dialog = DeleteTaskDialogFragment()
        dialog.arguments = bundleOf("taskData" to task)
        dialog.show(getParentFragmentManager(), DeleteTaskDialogFragment.TAG)
    }

    private fun onTaskEdited() {
        Toast.makeText(requireContext(), R.string.toast_task_updated, Toast.LENGTH_SHORT).show()
        loadTasks()
    }

    private fun onTaskDeleted() {
        Toast.makeText(requireContext(), R.string.toast_task_deleted, Toast.LENGTH_SHORT).show()
        loadTasks()
    }

}