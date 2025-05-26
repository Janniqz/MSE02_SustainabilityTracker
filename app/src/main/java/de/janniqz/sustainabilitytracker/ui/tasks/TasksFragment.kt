package de.janniqz.sustainabilitytracker.ui.tasks

import AppDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import de.janniqz.sustainabilitytracker.R
import de.janniqz.sustainabilitytracker.data.model.entity.TaskEntity
import de.janniqz.sustainabilitytracker.databinding.FragmentTasksBinding
import kotlinx.coroutines.launch

class TasksFragment : Fragment() {

    private lateinit var binding: FragmentTasksBinding
    private lateinit var taskListAdapter: ArrayAdapter<TaskEntity>
    private lateinit var database: AppDatabase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTasksBinding.inflate(inflater, container, false)
        database = AppDatabase.getInstance(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.fabNewTask.setOnClickListener { view ->
            findNavController().navigate(R.id.action_createTask)
        }

        lifecycleScope.launch {
            taskListAdapter = TaskDisplayAdapter(requireContext(), database.task().getAll(), ::onEditTask, ::onCompleteTask, ::onDeleteTask)
            binding.taskList.adapter = taskListAdapter
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