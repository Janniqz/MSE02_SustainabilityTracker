package de.janniqz.sustainabilitytracker.data.model;

import de.janniqz.sustainabilitytracker.data.model.entity.TaskCompletionEntity;
import de.janniqz.sustainabilitytracker.data.model.entity.TaskEntity;

/**
 * Container Class for packaging a Task with a single TaskCompletion for the Statistics Panel
 */
data class StatisticsCompletion(
    val task: TaskEntity,
    val completion: TaskCompletionEntity
)
