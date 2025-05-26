package de.janniqz.sustainabilitytracker.ui.tasks

import AppDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import de.janniqz.sustainabilitytracker.R
import de.janniqz.sustainabilitytracker.data.model.entity.TaskEntity
import de.janniqz.sustainabilitytracker.databinding.FragmentTasksBinding
import kotlinx.coroutines.launch

class TasksFragment : Fragment() {

    private lateinit var binding: FragmentTasksBinding
    private lateinit var taskListAdapter: TaskDisplayAdapter
    private lateinit var database: AppDatabase

    private var currentTasks: MutableList<TaskEntity> = mutableListOf()

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

        // Load tasks from the database
        loadTasks()
    }

    override fun onResume() {
        super.onResume()
        loadTasks()
    }

    private fun loadTasks() {
        lifecycleScope.launch {
            val tasksFromDb = database.task().getAll()
            currentTasks.clear()
            currentTasks.addAll(tasksFromDb)
            taskListAdapter.notifyDataSetChanged()
        }
    }

    private fun onEditTask(task: TaskEntity) {
        // TODO
    }

    private fun onCompleteTask(task: TaskEntity) {
        // TODO
    }

    private fun onDeleteTask(task: TaskEntity) {
        // TODO
    }

}