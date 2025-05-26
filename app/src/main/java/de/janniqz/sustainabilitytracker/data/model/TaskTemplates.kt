package de.janniqz.sustainabilitytracker.data.model

import de.janniqz.sustainabilitytracker.R

object TaskTemplates {

    val co2Templates = listOf(
        TaskTemplate(
            id = 1,
            name = "Carpooling",
            description = "Share a ride instead of driving alone.",
            category = TaskCategory.CO2,
            multiplier = 0.20f,
            requiredData = R.string.task_data_km
        ),
        TaskTemplate(
            id = 2,
            name = "Use Public Transport",
            description = "Take the bus or train instead of driving.",
            category = TaskCategory.CO2,
            multiplier = 0.12f,
            requiredData = R.string.task_data_km
        ),
        TaskTemplate(
            id = 3,
            name = "Eat a Vegetarian Meal",
            description = "Reduce your carbon footprint by skipping meat.",
            category = TaskCategory.CO2,
            multiplier = 15f,
            requiredData = null
        )
    )

    val waterTemplates = listOf(
        TaskTemplate(
            id = 4,
            name = "Take Shorter Showers",
            description = "Reduce water consumption during showers.",
            category = TaskCategory.Water,
            multiplier = 8f,
            requiredData = R.string.task_data_shower_duration
        )
    )

    val wasteTemplates = listOf(
        TaskTemplate(
            id = 5,
            name = "Recycle Properly",
            description = "Sort recyclables correctly.",
            category = TaskCategory.Waste,
            multiplier = 0.1f,
            requiredData = R.string.task_data_item_no
        ),
        TaskTemplate(
            id = 6,
            name = "Avoid Single-Use Plastics",
            description = "Reduce reliance on disposable plastics.",
            category = TaskCategory.Waste,
            multiplier = 0.08f,
            requiredData = R.string.task_data_item_no
        )
    )

    fun getTemplatesByCategory(category: TaskCategory): List<TaskTemplate> {
        return when (category) {
            TaskCategory.CO2 -> co2Templates
            TaskCategory.Water -> waterTemplates
            TaskCategory.Waste -> wasteTemplates
        }
    }

    fun getTemplateById(id: Int): TaskTemplate? {
        return (co2Templates + waterTemplates + wasteTemplates).find { it.id == id }
    }
}
