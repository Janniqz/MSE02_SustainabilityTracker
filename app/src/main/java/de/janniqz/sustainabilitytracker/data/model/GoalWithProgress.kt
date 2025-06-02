package de.janniqz.sustainabilitytracker.data.model

import de.janniqz.sustainabilitytracker.data.model.entity.GoalEntity

data class GoalWithProgress(
    val goal: GoalEntity,
    val progress: Float
)