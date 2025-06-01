package de.janniqz.sustainabilitytracker.data.model;

import de.janniqz.sustainabilitytracker.data.model.entity.TaskCompletionEntity;
import de.janniqz.sustainabilitytracker.data.model.entity.TaskEntity;

data class StatisticsCompletion(
    val task: TaskEntity,
    val completion: TaskCompletionEntity
)
