package de.janniqz.sustainabilitytracker.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TaskTemplate(
    val name: String,
    val description: String,
    val category: TaskCategory,
    val multiplier: Float,
    val requiredData: Int?
): Parcelable