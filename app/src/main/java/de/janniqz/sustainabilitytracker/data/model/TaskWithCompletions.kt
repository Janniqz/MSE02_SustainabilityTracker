package de.janniqz.sustainabilitytracker.data.model

import de.janniqz.sustainabilitytracker.data.model.entity.TaskEntity

data class TaskWithCompletions(
    val task: TaskEntity,
    val completions: Int
)
