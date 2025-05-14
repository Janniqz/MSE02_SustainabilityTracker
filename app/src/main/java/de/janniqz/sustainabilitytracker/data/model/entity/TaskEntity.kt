package de.janniqz.sustainabilitytracker.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import de.janniqz.sustainabilitytracker.data.model.TaskCategory

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val category: TaskCategory,
    val multiplier: Float,
    val createdAt: Long
)