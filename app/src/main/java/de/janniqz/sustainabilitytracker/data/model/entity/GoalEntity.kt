package de.janniqz.sustainabilitytracker.data.model.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import de.janniqz.sustainabilitytracker.data.model.TaskCategory
import de.janniqz.sustainabilitytracker.data.model.TimePeriod
import kotlinx.parcelize.Parcelize

/**
 * Entity representing a Goal
 */
@Parcelize
@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var category: TaskCategory,
    var periodicity: TimePeriod,

    var name: String,
    var targetAmount: Float
) : Parcelable