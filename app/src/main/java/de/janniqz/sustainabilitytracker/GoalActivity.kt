package de.janniqz.sustainabilitytracker

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import de.janniqz.sustainabilitytracker.databinding.ActivityGoalBinding

class GoalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGoalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoalBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setupNavigation()
    }

    private fun setupNavigation() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}