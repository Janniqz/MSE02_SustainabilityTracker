package de.janniqz.sustainabilitytracker.data.model

data class TaskTemplate(
    val name: String,
    val description: String,
    val category: TaskCategory,
    val multiplier: Float
)
