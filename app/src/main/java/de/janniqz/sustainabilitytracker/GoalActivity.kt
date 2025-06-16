package de.janniqz.sustainabilitytracker

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import de.janniqz.sustainabilitytracker.databinding.ActivityGoalBinding

/**
 * Activity responsible for managing and viewing Goals
 */
class GoalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGoalBinding

    /**
     * Base Activity Setup
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoalBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setupNavigation()
    }

    /**
     * Configures Navigation to display the Back Arrow and allow Back Navigation.
     * Either option leads back to the MainActivity (Tasks + Statistics)
     */
    private fun setupNavigation() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    /**
     * Handles Back Navigation
     */
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