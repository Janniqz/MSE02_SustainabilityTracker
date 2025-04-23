package de.janniqz.sustainabilitytracker.ui.tasks

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.janniqz.sustainabilitytracker.R
import de.janniqz.sustainabilitytracker.databinding.FragmentCreateTaskBinding

class CreateTaskFragment : Fragment() {

    private lateinit var binding: FragmentCreateTaskBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCreateTaskBinding.inflate(inflater, container, false)

        setupButtons()

        return binding.root
    }

    private fun setupButtons() {
        binding.btnPredefined.btnTitle.setText(R.string.task_type_predefined_title)
        binding.btnPredefined.btnDesc.setText(R.string.task_type_predefined_desc)
        binding.btnPredefined.btnIcon.setImageResource(R.drawable.ic_task_predefined)
        binding.btnPredefined.btnRoot.setOnClickListener {

        }

        binding.btnCustom.btnTitle.setText(R.string.task_type_custom_title)
        binding.btnCustom.btnDesc.setText(R.string.task_type_custom_desc)
        binding.btnCustom.btnIcon.setImageResource(R.drawable.ic_task_custom)
        binding.btnCustom.btnRoot.setOnClickListener {

        }
    }

}