package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule

import androidx.room.Embedded
import androidx.room.Relation
import org.joda.time.DateTime

data class ScheduleWithDays(
    @Embedded
    val schedule: Schedule = Schedule(),

    @Relation(parentColumn = "id", entityColumn = "scheduleId")
    val workingDays: MutableList<WorkingDayWithTimes> = ArrayList()
) {
    fun containsWorkingDay(day: Int): Boolean {
        return workingDays.any { it.getDayOfMonth() == day }
    }

    fun getWorkingDay(day: Int): WorkingDayWithTimes? {
        return workingDays.find { it.getDayOfMonth() == day }
    }

    fun getWorkingTimes(day: Int): List<WorkingTime> {
        return getWorkingDay(day)?.workingTimes ?: arrayListOf()
    }

    fun addWorkingDay(day: Int) {
        if (containsWorkingDay(day)) {
            return
        }

        val now = DateTime.now()
        val date = if (now.dayOfMonth <= day) {
            DateTime().withDate(now.year, now.monthOfYear, day)
        } else {
            if (now.monthOfYear == 12) {
                DateTime().withDate(now.year + 1, now.monthOfYear, day)
            } else {
                DateTime().withDate(now.year, now.monthOfYear + 1, day)
            }
        }
        val dateLong = date.withTime(0, 0, 0, 0).millis
        workingDays.add(WorkingDayWithTimes(WorkingDay(dateLong = dateLong)))
    }

    companion object {
        const val WORKING_DAYS = "working days"
    }
}