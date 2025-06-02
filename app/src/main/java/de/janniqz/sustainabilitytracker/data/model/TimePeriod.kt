package de.janniqz.sustainabilitytracker.data.model

import java.util.Locale

enum class TimePeriod {
    WEEK,
    MONTH,
    YEAR;

    fun getDisplayName(): String {
        return this.name.lowercase(Locale.getDefault()).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }
}