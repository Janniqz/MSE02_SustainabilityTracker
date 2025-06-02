package de.janniqz.sustainabilitytracker.data.db.converter

import androidx.room.TypeConverter
import de.janniqz.sustainabilitytracker.data.model.TimePeriod

class TimePeriodConverter {

    @TypeConverter
    fun fromTimePeriod(value: TimePeriod): String {
        return value.name
    }

    @TypeConverter
    fun toTimePeriod(value: String): TimePeriod {
        return TimePeriod.valueOf(value)
    }

}