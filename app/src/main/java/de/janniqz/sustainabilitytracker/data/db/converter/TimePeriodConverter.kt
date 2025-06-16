package de.janniqz.sustainabilitytracker.data.db.converter

import androidx.room.TypeConverter
import de.janniqz.sustainabilitytracker.data.model.TimePeriod

/**
 * Converter facilitating Database Conversions from and to the TimePeriod Enum.
 * @see TimePeriod
 */
class TimePeriodConverter {

    /**
     * Converts from TimePeriod to String for saving into the Database
     */
    @TypeConverter
    fun fromTimePeriod(value: TimePeriod): String {
        return value.name
    }

    /**
     * Converts from String to TimePeriod for loading from the Database
     */
    @TypeConverter
    fun toTimePeriod(value: String): TimePeriod {
        return TimePeriod.valueOf(value)
    }

}