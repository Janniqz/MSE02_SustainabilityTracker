package de.janniqz.sustainabilitytracker.tools

import android.icu.util.Calendar

interface ICalendar {
    /**
     * Returns a Calendar instance representing the current moment.
     */
    fun getCurrentCalendar(): Calendar

    /**
     * Returns the current time in milliseconds.
     */
    fun getCurrentTimeMillis(): Long
}

class SystemCalendar : ICalendar {
    override fun getCurrentCalendar(): Calendar {
        return Calendar.getInstance()
    }

    override fun getCurrentTimeMillis(): Long {
        return System.currentTimeMillis()
    }
}

// Test implementation
class FakeCalendar(private val calendar: Calendar) : ICalendar {
    override fun getCurrentCalendar(): Calendar {
        return calendar.clone() as Calendar
    }

    override fun getCurrentTimeMillis(): Long {
        return getCurrentCalendar().timeInMillis
    }
}