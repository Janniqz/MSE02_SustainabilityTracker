package de.janniqz.sustainabilitytracker.tools

import android.icu.util.Calendar
import de.janniqz.sustainabilitytracker.data.model.TimePeriod

/**
 * Static class providing various helper functions related to Dates
 */
class DateHelper {
    companion object {
        /**
         * Resets the Calendar to the start of the day
         */
        fun Calendar.setToBeginningOfDay(): Calendar {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            return this
        }

        /**
         * Returns a Time Range depending on the passed Periodicity + Focus Date
         * @param periodicity Periodicity for which the Date Range should be retrieved
         * @param focusDate Calendar Date the Time Range should be based on
         * @return Start Date / End Date for passed periodicity
         */
        fun getCurrentPeriodicityRange(periodicity: TimePeriod, focusDate: Calendar): Pair<Long, Long> {
            val calendar = (focusDate.clone() as Calendar).setToBeginningOfDay()
            var startDate = 0L
            var endDate = 0L

            when (periodicity) {
                TimePeriod.WEEK -> {
                    // Start of first day of week
                    calendar.add(Calendar.DAY_OF_WEEK, 2 - calendar.get(Calendar.DAY_OF_WEEK))  // Monday
                    startDate = calendar.timeInMillis

                    // End of last day of week
                    calendar.add(Calendar.WEEK_OF_YEAR, 1)
                    calendar.add(Calendar.MILLISECOND, -1)
                    endDate = calendar.timeInMillis
                }
                TimePeriod.MONTH -> {
                    // Start of first day of month
                    calendar.set(Calendar.DAY_OF_MONTH, 1)
                    startDate = calendar.timeInMillis

                    // End of last day of month
                    calendar.add(Calendar.MONTH, 1)
                    calendar.add(Calendar.MILLISECOND, -1)
                    endDate = calendar.timeInMillis
                }
                TimePeriod.YEAR -> {
                    // Start of first day of year
                    calendar.set(Calendar.DAY_OF_YEAR, 1)
                    startDate = calendar.timeInMillis

                    // End of last day of year
                    calendar.add(Calendar.YEAR, 1)
                    calendar.add(Calendar.MILLISECOND, -1) // End of the last day of the year
                    endDate = calendar.timeInMillis
                }
            }

            return Pair(startDate, endDate)
        }
    }
}