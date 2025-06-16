package de.janniqz.sustainabilitytracker.data.model

import de.janniqz.sustainabilitytracker.data.model.entity.GoalEntity

/**
 * Container Class for packaging a Goal with its current completion rate
 * @see GoalEntity
 */
data class GoalWithProgress(
    val goal: GoalEntity,
    val progress: Float
)