package de.janniqz.sustainabilitytracker.data.model.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import de.janniqz.sustainabilitytracker.data.model.TaskCategory
import de.janniqz.sustainabilitytracker.data.model.TaskType
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val type: TaskType,
    var name: String,
    val category: TaskCategory,
    var savings: Float,
    val createdAt: Long,

    // Predefined only
    val templateId: Int? = null
) : Parcelable