package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule

import androidx.room.Embedded
import androidx.room.Relation
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.WorkingDay.Companion.DATE_DELIMITER
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
            "$day$DATE_DELIMITER${now.monthOfYear}$DATE_DELIMITER${now.year}"
        } else {
            if (now.monthOfYear == 12) {
                "$day${DATE_DELIMITER}1$DATE_DELIMITER${now.year + 1}"
            } else {
                "$day$DATE_DELIMITER${now.monthOfYear + 1}$DATE_DELIMITER${now.year}"
            }
        }
        workingDays.add(WorkingDayWithTimes(WorkingDay(date = date)))
    }

    companion object {
        const val WORKING_DAYS = "working days"
    }
}