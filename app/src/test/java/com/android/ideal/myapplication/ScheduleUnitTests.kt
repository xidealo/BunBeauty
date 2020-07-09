package com.android.ideal.myapplication

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.schedule.ScheduleInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.ScheduleRepository
import io.mockk.mockk
import org.joda.time.DateTime
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mock


class ScheduleUnitTests {

    @Mock
    private val scheduleRepository: ScheduleRepository = mockk()

    private val scheduleInteractor = ScheduleInteractor(scheduleRepository)

    @Test
    fun isGettingDaysBetweenCorrect() {
        val expectedDaysBetween = 30
        val startDate = DateTime().withDate(2020, 1, 10)
        val endDate = DateTime().withDate(2020, 2, 9)

        val daysBetween = scheduleInteractor.getDaysBetween(startDate, endDate)

        assertEquals(expectedDaysBetween, daysBetween)
    }

    @Test
    fun isGettingDateFromStringCorrect() {
        val expectedDate = DateTime().withDate(2020, 1, 10)
        val dateString = "10-1-2020"

        val date = scheduleInteractor.getDateFromString(dateString)

        assertEquals(expectedDate.dayOfMonth, date.dayOfMonth)
        assertEquals(expectedDate.monthOfYear, date.monthOfYear)
        assertEquals(expectedDate.year, date.year)
    }


}