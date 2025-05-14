package de.janniqz.sustainabilitytracker.data.db.converter

import androidx.room.TypeConverter
import de.janniqz.sustainabilitytracker.data.model.TaskCategory

class TaskCategoryConverter {

    @TypeConverter
    fun fromTaskCategory(value: TaskCategory): String {
        return value.name
    }

    @TypeConverter
    fun toTaskCategory(value: String): TaskCategory {
        return TaskCategory.valueOf(value)
    }

}