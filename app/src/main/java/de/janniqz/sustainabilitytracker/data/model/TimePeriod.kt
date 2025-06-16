package de.janniqz.sustainabilitytracker.data.model

import java.util.Locale

/**
 * Enumeration describing the different Time Periodicities for Task Statistics / Goals
 */
enum class TimePeriod {
    WEEK,
    MONTH,
    YEAR;

    /**
     * Returns a UI friendly representation of the current Enum Value
     */
    fun getDisplayName(): String {
        return this.name.lowercase(Locale.getDefault()).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }
}