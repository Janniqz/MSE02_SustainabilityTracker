package de.janniqz.sustainabilitytracker.ui.tasks

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import de.janniqz.sustainabilitytracker.R
import de.janniqz.sustainabilitytracker.databinding.FragmentCreatePredefinedTaskBinding

class CreatePredefinedTaskFragment : Fragment() {

    private lateinit var binding: FragmentCreatePredefinedTaskBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCreatePredefinedTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.categorySelection.btnCo2.isChecked = true
    }
}