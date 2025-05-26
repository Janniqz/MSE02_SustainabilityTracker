package de.janniqz.sustainabilitytracker.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import de.janniqz.sustainabilitytracker.data.model.TaskCategory
import de.janniqz.sustainabilitytracker.data.model.TaskType

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val type: TaskType,
    val name: String,
    val category: TaskCategory,
    val savings: Float,
    val createdAt: Long,

    // Predefined only
    val templateId: Int? = null
)