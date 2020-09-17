package com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule

import androidx.room.Embedded
import androidx.room.Relation
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule.Schedule.Companion.MONTHS
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

    fun getFreeWorkingTimes(dayOfMonth: Int): List<WorkingTime> {
        return workingTimeList.filter { it.orderId.isEmpty() && it.getDayOfMonth() == dayOfMonth }
    }

    fun containsWorkingTime(day: Int, workingTime: WorkingTime): Boolean {
        return workingTimeList.any {
            it.getDayOfMonth() == day &&
                    it.getHour() == workingTime.getHour() &&
                    it.getMinutes() == workingTime.getMinutes()
        }
    }

    fun containsWorkingTime(workingTime: WorkingTime?): Boolean {
        if (workingTime == null) {
            return false
        }

        return workingTimeList.any { it.time == workingTime.time }
    }

    fun addWorkingTime(day: Int, timeString: String): WorkingTime? {
        val gettingDateTime = DateTime(schedule.gettingTime)
        val date = if (gettingDateTime.dayOfMonth <= day) {
            gettingDateTime.withDayOfMonth(day)
        } else {
            if (gettingDateTime.monthOfYear == 12) {
                DateTime().withDate(gettingDateTime.year + 1, gettingDateTime.monthOfYear, day)
            } else {
                DateTime().withDate(gettingDateTime.year, gettingDateTime.monthOfYear + 1, day)
            }
        }
        val timeParts = timeString.split(WorkingTime.TIME_DELIMITER)
        val time = date.withTime(timeParts[0].toInt(), timeParts[1].toInt(), 0, 0).millis

        return if (!workingTimeList.any { it.time == time }) {
            val workingTime = WorkingTime(time = time)
            workingTimeList.add(workingTime)

            workingTime
        } else {
            null
        }
    }

    fun addWorkingTime(workingTime: WorkingTime?) {
        if (workingTime != null) {
            workingTimeList.add(workingTime)
        }
    }

    fun removeWorkingTime(day: Int, timeString: String): WorkingTime? {
        val timeParts = timeString.split(WorkingTime.TIME_DELIMITER)
        val workingTime = workingTimeList.find {
            it.getDayOfMonth() == day &&
                    it.getHour() == timeParts[0].toInt() &&
                    it.getMinutes() == timeParts[1].toInt()
        }!!
        return if (workingTime.orderId.isEmpty()) {
            workingTimeList.remove(workingTime)
            workingTime
        } else {
            null
        }
    }

    fun removeWorkingTime(workingTime: WorkingTime) {
        val removingWorkingTime = workingTimeList.find { it.time == workingTime.time }
        workingTimeList.remove(removingWorkingTime)
    }

    fun getFutureSchedule(): ScheduleWithWorkingTime {
        return ScheduleWithWorkingTime(
            schedule,
            ArrayList(workingTimeList.filter { it.time > schedule.gettingTime }
                .sortedBy { it.time })
        )
    }

    fun getFutureDaySchedule(): ScheduleWithWorkingTime {
        val filteredWorkingTime = workingTimeList.filter {
            val date = DateTime(it.time)
            val currentDate = DateTime(schedule.gettingTime)
            date.year > currentDate.year || date.dayOfYear >= currentDate.dayOfYear
        }

        return ScheduleWithWorkingTime(
            schedule,
            ArrayList(filteredWorkingTime.sortedBy { it.time })
        )
    }

    fun getPastDaySchedule(): ScheduleWithWorkingTime {
        val filteredWorkingTime = workingTimeList.filter {
            val date = DateTime(it.time)
            val currentDate = DateTime(schedule.gettingTime)
            date.year <= currentDate.year && date.dayOfYear < currentDate.dayOfYear
        }

        return ScheduleWithWorkingTime(
            schedule,
            ArrayList(filteredWorkingTime.sortedBy { it.time })
        )
    }

    fun getAvailableDays(serviceDuration: Float): List<WorkingDay> {
        val availableDays: MutableSet<WorkingDay> = HashSet()

        var timeInRaw = 0
        var previousTime: WorkingTime? = null
        val freeWorkingTimeList = workingTimeList.filter { it.orderId.isEmpty() }
        for (time in freeWorkingTimeList) {
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
                val workingDay = WorkingDay(
                    dayOfMonth = time.getDayOfMonth(),
                    month = MONTHS.getValue(time.getMonth())
                )
                availableDays.add(workingDay)
            }
        }

        return availableDays.sortedBy { it.dayOfMonth }
    }

    fun getSessions(serviceDuration: Float, dayOfMonth: Int): List<Session> {
        if (isEmpty()) {
            return listOf()
        }

        val sessionList = ArrayList<Session>()
        val dayTimeList = getFreeWorkingTimes(dayOfMonth)
        var timeInRaw = 0
        lateinit var previousTime: WorkingTime
        for (time in dayTimeList) {
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

    fun haveSomeOrder(days: List<Int>, timeString: String): Boolean {
        return days.any { day ->
            getWorkingTimes(day).find { workingTime ->
                workingTime.getStringTime() == timeString
            }!!.orderId.isNotEmpty()
        }
    }
}