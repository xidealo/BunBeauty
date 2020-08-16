package com.android.ideal.myapplication

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.Session
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.WorkingDayWithTimes
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.WorkingTime
import org.joda.time.DateTime
import org.junit.Assert.*
import org.junit.Test
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.WorkingDay as WorkingDay1

class WorkingDayUnitTests {

    @Test
    fun isCheckingAvailableDayCorrect() {
        val duration = 1.5f
        val timeList = arrayListOf(
            getWorkingTimeByHourAndMinute(7, 0),
            getWorkingTimeByHourAndMinute(8, 30),
            getWorkingTimeByHourAndMinute(9, 30),
            getWorkingTimeByHourAndMinute(10, 0),
            getWorkingTimeByHourAndMinute(10, 30),
            getWorkingTimeByHourAndMinute(12, 30)
        )
        val workingDayWithTimes = WorkingDayWithTimes(WorkingDay1(dayOfMonth = 0L), timeList)

        assertTrue(workingDayWithTimes.isAvailable(duration))
    }

    @Test
    fun isCheckingNotAvailableDayCorrect() {
        val duration = 1.5f
        val timeList = arrayListOf<WorkingTime>(
            getWorkingTimeByHourAndMinute(7, 30),
            getWorkingTimeByHourAndMinute(8, 0),
            getWorkingTimeByHourAndMinute(9, 0),
            getWorkingTimeByHourAndMinute(9, 30),
            getWorkingTimeByHourAndMinute(10, 30),
            getWorkingTimeByHourAndMinute(11, 0)
        )
        val workingDayWithTimes = WorkingDayWithTimes(WorkingDay1(dayOfMonth = 0L), timeList)

        assertFalse(workingDayWithTimes.isAvailable(duration))
    }

    @Test
    fun isGettingSessionsCorrect() {
        val duration = 1.0f
        val timeList = arrayListOf<WorkingTime>(
            getWorkingTimeByHourAndMinute(7, 30),
            getWorkingTimeByHourAndMinute(8, 0),
            getWorkingTimeByHourAndMinute(8, 30),
            getWorkingTimeByHourAndMinute(9, 0),
            getWorkingTimeByHourAndMinute(9, 30),
            getWorkingTimeByHourAndMinute(10, 30),
            getWorkingTimeByHourAndMinute(11, 0)
        )
        val workingDayWithTimes = WorkingDayWithTimes(WorkingDay1(dayOfMonth = 0L), timeList)
        val expectedSessions = arrayListOf(
            getSessionByHourAndMinute(7, 30, 8, 30),
            getSessionByHourAndMinute(8, 30, 9, 30),
            getSessionByHourAndMinute(10, 30, 11, 30)
        )

        val sessions = workingDayWithTimes.getSessions(duration)

        assertEquals(expectedSessions, sessions)
    }

    @Test
    fun isDayComparisonCorrect() {
        val workingDayList = arrayListOf(
            getWorkingDayByDayMonthAndYear(22, 8, 2020),
            getWorkingDayByDayMonthAndYear(3, 9, 2020),
            getWorkingDayByDayMonthAndYear(23, 8, 2020),
            getWorkingDayByDayMonthAndYear(2, 8, 2020)
        )
        val expectedSortedWorkingDayList = arrayListOf(
            getWorkingDayByDayMonthAndYear(2, 8, 2020),
            getWorkingDayByDayMonthAndYear(22, 8, 2020),
            getWorkingDayByDayMonthAndYear(23, 8, 2020),
            getWorkingDayByDayMonthAndYear(3, 9, 2020)
        )

        val sortedWorkingDayList = workingDayList.sortedBy { it.dayOfMonth }

        assertEquals(expectedSortedWorkingDayList, sortedWorkingDayList)
    }


    private fun getWorkingTimeByHourAndMinute(hour: Int, minute: Int): WorkingTime {
        return WorkingTime(
            time = DateTime()
                .withDate(2020, 1, 1)
                .withTime(hour, minute, 0, 0)
                .millis
        )
    }

    private fun getSessionByHourAndMinute(
        startHour: Int,
        startMinute: Int,
        finishHour: Int,
        finishMinute: Int
    ): Session {
        return Session(
            startTime = DateTime()
                .withDate(2020, 1, 1)
                .withTime(startHour, startMinute, 0, 0)
                .millis,
            finishTime = DateTime()
                .withDate(2020, 1, 1)
                .withTime(finishHour, finishMinute, 0, 0)
                .millis
        )
    }

    private fun getWorkingDayByDayMonthAndYear(day: Int, month: Int, year: Int): WorkingDay1 {
        return WorkingDay1(
            dayOfMonth = DateTime()
                .withDate(year, month, day)
                .withTime(0, 0, 0, 0)
                .millis
        )
    }

}