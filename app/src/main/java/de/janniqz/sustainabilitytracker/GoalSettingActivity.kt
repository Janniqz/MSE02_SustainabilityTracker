package de.janniqz.sustainabilitytracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import de.janniqz.sustainabilitytracker.databinding.ActivityGoalSettingBinding

class GoalSettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGoalSettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoalSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}