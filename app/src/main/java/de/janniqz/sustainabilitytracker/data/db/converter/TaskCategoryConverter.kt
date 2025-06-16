package de.janniqz.sustainabilitytracker.data.db.converter

import androidx.room.TypeConverter
import de.janniqz.sustainabilitytracker.data.model.TaskCategory

/**
 * Converter facilitating Database Conversions from and to the TaskCategory Enum.
 * @see TaskCategory
 */
class TaskCategoryConverter {

    /**
     * Converts from TaskCategory to String for saving into the Database
     */
    @TypeConverter
    fun fromTaskCategory(value: TaskCategory): String {
        return value.name
    }

    /**
     * Converts from String to TaskCategory for loading from the Database
     */
    @TypeConverter
    fun toTaskCategory(value: String): TaskCategory {
        return TaskCategory.valueOf(value)
    }

}