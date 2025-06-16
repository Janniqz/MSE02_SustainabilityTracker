package de.janniqz.sustainabilitytracker.data.db.converter

import androidx.room.TypeConverter
import de.janniqz.sustainabilitytracker.data.model.TaskType

/**
 * Converter facilitating Database Conversions from and to the TaskType Enum.
 * @see TaskType
 */
class TaskTypeConverter {

    /**
     * Converts from TimePeriod to String for saving into the Database
     */
    @TypeConverter
    fun fromTaskType(value: TaskType): String {
        return value.name
    }

    /**
     * Converts from String to TaskCategory for loading from the Database
     */
    @TypeConverter
    fun toTaskType(value: String): TaskType {
        return TaskType.valueOf(value)
    }

}