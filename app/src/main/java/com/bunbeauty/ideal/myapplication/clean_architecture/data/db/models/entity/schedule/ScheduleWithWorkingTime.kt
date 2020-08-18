package com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule

import androidx.room.Embedded
import androidx.room.Relation
import org.joda.time.DateTime

data class ScheduleWithWorkingTime(
    @Embedded
    val schedule: Schedule = Schedule(),

    @Relation(parentColumn = "id", entityColumn = "scheduleId")
    val workingTimeList: MutableList<WorkingTime> = ArrayList()
) {
    fun containsWorkingDay(day: Int): Boolean {
        return workingTimeList.any { it.getDayOfMonth() == day }
    }

    fun getWorkingTimes(dayOfMonth: Int): List<WorkingTime> {
        return workingTimeList.filter { it.getDayOfMonth() == dayOfMonth }
    }

    fun containsWorkingTime(day: Int, workingTime: WorkingTime): Boolean {
        return workingTimeList.any {
            it.getDayOfMonth() == day &&
                    it.getHour() == workingTime.getHour() &&
                    it.getMinutes() == workingTime.getMinutes()
        }
    }

    fun addWorkingTime(day: Int, timeString: String) {
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
        val timeParts = timeString.split(WorkingTime.TIME_DELIMITER)
        val time = date.withTime(timeParts[0].toInt(), timeParts[1].toInt(), 0, 0).millis

        if (!workingTimeList.any { it.time == time }) {
            workingTimeList.add(WorkingTime(time = time))
        }
    }

    fun removeTime(day: Int, timeString: String) {
        val timeParts = timeString.split(WorkingTime.TIME_DELIMITER)
        val workingTime = workingTimeList.find {
            it.getDayOfMonth() == day &&
                    it.getHour() == timeParts[0].toInt() &&
                    it.getMinutes() == timeParts[1].toInt()
        }
        workingTimeList.remove(workingTime)
    }

    fun getAvailableDays(serviceDuration: Float): Set<WorkingDay> {
        val availableDays: MutableSet<WorkingDay> = HashSet()

        var timeInRaw = 0
        var previousTime: WorkingTime? = null
        for (time in workingTimeList) {
            if (timeInRaw == 0) {
                timeInRaw = 1
            } else {
                if (time.isNext(previousTime)) {
                    timeInRaw++
                } else {
                    timeInRaw = 1
                }
            }
            previousTime = time

            if ((serviceDuration / 0.5f).toInt() == timeInRaw) {
                availableDays.add(WorkingDay(dayOfMonth = time.getDayOfMonth()))
            }
        }

        return availableDays
    }

    fun getSessions(serviceDuration: Float, dayOfMonth: Int): List<Session> {
        if (isEmpty()) {
            return listOf()
        }

        val sessionList = ArrayList<Session>()
        val filteredTimeList = getWorkingTimes(dayOfMonth)
        var timeInRaw = 0
        lateinit var previousTime: WorkingTime
        for (time in filteredTimeList) {
            if (timeInRaw == 0) {
                timeInRaw = 1
            } else {
                if (time.isNext(previousTime)) {
                    timeInRaw++
                } else {
                    timeInRaw = 1
                }
            }
            previousTime = time

            if ((serviceDuration / 0.5f).toInt() == timeInRaw) {
                val session = Session(time.getTimeBefore(timeInRaw), time.getFinishTime())
                sessionList.add(session)
                timeInRaw = 0
            }
        }

        return sessionList
    }

    fun isEmpty(): Boolean {
        return workingTimeList.isEmpty()
    }
}