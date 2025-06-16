package de.janniqz.sustainabilitytracker

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import de.janniqz.sustainabilitytracker.databinding.ActivityMainBinding

/**
 * Activity responsible for managing and viewing Tasks + Statistics
 */
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    /**
     * Base Activity Setup
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setupNavigation()
    }

    /**
     * Sets up the Top Bar Menu (Back + Name + Goals)
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    /**
     * Sets up Navigation Events and configures the Action Bar
     */
    private fun setupNavigation() {
        val navController = findNavController(R.id.nav_host_fragment)
        navController.addOnDestinationChangedListener { _, destination, _ -> onNavigationChanged(destination.id) }

        appBarConfiguration = AppBarConfiguration(setOf(R.id.nav_tasks, R.id.nav_statistics))
        setupActionBarWithNavController(navController, appBarConfiguration)

        val bottomNav = binding.bottomNav
        bottomNav.setupWithNavController(navController)
    }

    /**
     * Shows / Hides the Bottom Navigation Bar depending on the currently active Fragment.
     * Bar is shown only if in the main Task / Statistics Fragment.
     */
    private fun onNavigationChanged(destinationId: Int) {
        val shouldShow = when (destinationId) {
            R.id.nav_tasks, R.id.nav_statistics -> true
            else -> false
        }

        binding.bottomNav.animate()
            .alpha(if (shouldShow) 1f else 0f)
            .setDuration(100)
            .withStartAction { binding.bottomNav.visibility = View.VISIBLE }
            .withEndAction { if (!shouldShow) binding.bottomNav.visibility = View.GONE }
            .start()
    }

    /**
     * Handles Back Navigation
     */
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    /**
     * Handles Navigation to the Goals Activity (via Goals Button)
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_set_goals -> {
                startActivity(Intent(this, GoalActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}