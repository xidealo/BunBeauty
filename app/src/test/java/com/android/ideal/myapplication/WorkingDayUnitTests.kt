package com.android.ideal.myapplication

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.Session
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.WorkingDay
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.WorkingDayWithTimes
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.WorkingTime
import org.junit.Assert.*
import org.junit.Test

class WorkingDayUnitTests {

    @Test
    fun isCheckingAvailableDayCorrect() {
        val duration = 1.5f
        val timeList = arrayListOf(
            WorkingTime(time = "7:00"),
            WorkingTime(time = "8:00"),
            WorkingTime(time = "8:30"),
            WorkingTime(time = "9:30"),
            WorkingTime(time = "10:00"),
            WorkingTime(time = "10:30")
        )
        val workingDayWithTimes = WorkingDayWithTimes(WorkingDay(), timeList)

        assertTrue(workingDayWithTimes.isAvailable(duration))
    }

    @Test
    fun isCheckingNotAvailableDayCorrect() {
        val duration = 1.5f
        val timeList = arrayListOf(
            WorkingTime(time = "7:30"),
            WorkingTime(time = "8:00"),
            WorkingTime(time = "9:00"),
            WorkingTime(time = "9:30"),
            WorkingTime(time = "10:30"),
            WorkingTime(time = "11:00")
        )
        val workingDayWithTimes = WorkingDayWithTimes(WorkingDay(), timeList)

        assertFalse(workingDayWithTimes.isAvailable(duration))
    }

    @Test
    fun isGettingSessionsCorrect() {
        val duration = 1.0f
        val timeList = arrayListOf(
            WorkingTime(time = "7:30"),
            WorkingTime(time = "8:00"),
            WorkingTime(time = "8:30"),
            WorkingTime(time = "9:00"),
            WorkingTime(time = "9:30"),
            WorkingTime(time = "10:30"),
            WorkingTime(time = "11:00")
        )
        val workingDayWithTimes = WorkingDayWithTimes(WorkingDay(), timeList)
        val expectedSessions = arrayListOf(
            Session("7:30", "8:30"),
            Session("8:30", "9:30"),
            Session("10:30", "11:30")
        )

        val sessions = workingDayWithTimes.getSessions(duration)

        assertEquals(expectedSessions, sessions)
    }

    @Test
    fun isDayComparisonCorrect() {
        val workingDayList = arrayListOf(
            WorkingDay(date = "22-8-2020"),
            WorkingDay(date = "3-9-2020"),
            WorkingDay(date = "23-8-2020"),
            WorkingDay(date = "2-8-2020")
        )
        val expectedSortedWorkingDayList = arrayListOf(
            WorkingDay(date = "2-8-2020"),
            WorkingDay(date = "22-8-2020"),
            WorkingDay(date = "23-8-2020"),
            WorkingDay(date = "3-9-2020")
        )

        val sortedWorkingDayList = workingDayList.sortedBy {
            it.getDateForComparison()
        }

        assertEquals(expectedSortedWorkingDayList, sortedWorkingDayList)
    }

}