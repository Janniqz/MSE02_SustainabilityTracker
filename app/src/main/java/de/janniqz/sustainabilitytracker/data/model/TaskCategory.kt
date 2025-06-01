package de.janniqz.sustainabilitytracker.data.model

import de.janniqz.sustainabilitytracker.R

enum class TaskCategory {
    CO2,
    Water,
    Waste;

    companion object {
        fun getTaskIcon(category: TaskCategory): Int {
            return when (category) {
                CO2 -> R.drawable.ic_category_co2
                Water -> R.drawable.ic_category_water
                Waste -> R.drawable.ic_category_waste
            }
        }

        fun getSavingsUnit(category: TaskCategory): Int {
            return when (category) {
                CO2 -> R.string.task_category_co2_unit
                Water -> R.string.task_category_water_unit
                Waste -> R.string.task_category_waste_unit
            }
        }
    }
}