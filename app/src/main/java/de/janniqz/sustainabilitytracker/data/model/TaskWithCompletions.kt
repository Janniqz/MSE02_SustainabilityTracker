package de.janniqz.sustainabilitytracker.data.model

import de.janniqz.sustainabilitytracker.data.model.entity.TaskEntity

/**
 * Container Class for packaging a Task with its number of completions
 * @see TaskEntity
 * @see de.janniqz.sustainabilitytracker.data.model.entity.TaskCompletionEntity
 */
data class TaskWithCompletions(
    val task: TaskEntity,
    val completions: Int
)
