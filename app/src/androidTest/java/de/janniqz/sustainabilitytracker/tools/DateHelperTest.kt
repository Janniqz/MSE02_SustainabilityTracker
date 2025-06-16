package de.janniqz.sustainabilitytracker.tools

import android.icu.util.Calendar
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.janniqz.sustainabilitytracker.data.model.TimePeriod
import de.janniqz.sustainabilitytracker.tools.DateHelper.Companion.setToBeginningOfDay
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DateHelperTest {

    @Test
    fun getCurrentPeriodicityRange_forWeek_returnsCorrectRange() {
        // ARRANGE
        val focusCalendar = Calendar.getInstance().apply {
            set(2025, Calendar.JUNE, 18) // Wednesday, June 18, 2025
        }
        focusCalendar.firstDayOfWeek = Calendar.MONDAY

        // ACT
        val (startDate, endDate) = DateHelper.Companion.getCurrentPeriodicityRange(TimePeriod.WEEK, focusCalendar)

        // ASSERT
        val expectedStartCalendar = Calendar.getInstance().apply {
            set(2025, Calendar.JUNE, 16, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val expectedEndCalendar = Calendar.getInstance().apply {
            set(2025, Calendar.JUNE, 22, 23, 59, 59)
            set(Calendar.MILLISECOND, 999)
        }

        Assert.assertEquals(expectedStartCalendar.timeInMillis, startDate)
        Assert.assertEquals(expectedEndCalendar.timeInMillis, endDate)
    }

    @Test
    fun getCurrentPeriodicityRange_forMonth_returnsCorrectRange() {
        // ARRANGE
        val focusCalendar = Calendar.getInstance().apply { set(2025, Calendar.JUNE, 16) }

        // ACT
        val (startDate, endDate) = DateHelper.Companion.getCurrentPeriodicityRange(TimePeriod.MONTH, focusCalendar)

        // ASSERT
        val expectedStartCalendar = Calendar.getInstance().apply {
            set(2025, Calendar.JUNE, 1, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val expectedEndCalendar = Calendar.getInstance().apply {
            set(2025, Calendar.JUNE, 30, 23, 59, 59)
            set(Calendar.MILLISECOND, 999)
        }
        Assert.assertEquals(expectedStartCalendar.timeInMillis, startDate)
        Assert.assertEquals(expectedEndCalendar.timeInMillis, endDate)
    }

    @Test
    fun getCurrentPeriodicityRange_forYear_returnsCorrectRange() {
        // ARRANGE
        val focusCalendar = Calendar.getInstance().apply { set(2025, Calendar.JUNE, 16) }

        // ACT
        val (startDate, endDate) = DateHelper.Companion.getCurrentPeriodicityRange(TimePeriod.YEAR, focusCalendar)

        // ASSERT
        val expectedStartCalendar = Calendar.getInstance().apply {
            set(2025, Calendar.JANUARY, 1, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val expectedEndCalendar = Calendar.getInstance().apply {
            set(2025, Calendar.DECEMBER, 31, 23, 59, 59)
            set(Calendar.MILLISECOND, 999)
        }
        Assert.assertEquals(expectedStartCalendar.timeInMillis, startDate)
        Assert.assertEquals(expectedEndCalendar.timeInMillis, endDate)
    }

    @Test
    fun setCalendarToBeginningOfDay_zeroesOut() {
        // ARRANGE
        val calendar = Calendar.getInstance().apply {
            set(2025, Calendar.JUNE, 16, 14, 30, 55)
            set(Calendar.MILLISECOND, 123)
        }

        // ACT
        calendar.setToBeginningOfDay()

        // ASSERT
        Assert.assertEquals(0, calendar.get(Calendar.HOUR_OF_DAY))
        Assert.assertEquals(0, calendar.get(Calendar.MINUTE))
        Assert.assertEquals(0, calendar.get(Calendar.SECOND))
        Assert.assertEquals(0, calendar.get(Calendar.MILLISECOND))
    }
}