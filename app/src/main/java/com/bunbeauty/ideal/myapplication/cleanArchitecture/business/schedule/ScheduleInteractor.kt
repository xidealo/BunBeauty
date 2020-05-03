package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.schedule

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.schedule.SchedulePresenterCallback
import org.joda.time.DateTime

class ScheduleInteractor {

    var selectedDayIndexes = ArrayList<Int>()
    var selectedDays = ArrayList<Int>()
    private val schedule = HashMap<Int, MutableSet<String>>() // <dayOfMonth, list of time>

    fun getDateString(dayIndex: Int): String {
        val dayOfWeek = DateTime.now().dayOfWeek - 1
        val lastMonday = DateTime.now().minusDays(dayOfWeek)
        val date = lastMonday.plusDays(dayIndex).dayOfMonth

        return date.toString()
    }

    fun getTineString(timeIndex: Int): String {
        val hours = timeIndex / 2
        val minutes = (timeIndex % 2) * 30
        val minutesString = if (minutes == 0) {
            "00"
        } else {
            minutes.toString()
        }

        return "$hours:$minutesString"
    }

    fun isPastDay(dayIndex: Int): Boolean {
        val dayOfWeek = DateTime.now().dayOfWeek - 2

        return dayIndex >= dayOfWeek
    }

    fun getTime(schedulePresenterCallback: SchedulePresenterCallback) {
        val accurateTime = LinkedHashSet<String>()
        val inaccurateTime = LinkedHashSet<String>()

        for (day in selectedDays) {
            if (!schedule.containsKey(day)) {
                continue
            }

            for (time in schedule[day]!!) {
                if (isAccurate(time)) {
                    accurateTime.add(time)
                } else {
                    inaccurateTime.add(time)
                }
            }
        }

        schedulePresenterCallback.showAccurateTime(accurateTime)
        schedulePresenterCallback.showInaccurateTime(inaccurateTime)
    }

    private fun isAccurate(time: String): Boolean {
        for (day in selectedDays) {
            if (!schedule.containsKey(day)) {
                return false
            }

            if (!schedule[day]!!.contains(time)) {
                return false
            }
        }

        return true
    }

    fun addToSchedule(
        days: List<Int>,
        time: String,
        schedulePresenterCallback: SchedulePresenterCallback
    ) {
        for ((i, day) in days.withIndex()) {
            if (schedule.keys.contains(day)) {
                schedule[day]!!.add(time)
            } else {
                schedule[day] = mutableSetOf(time)
            }

            if (schedule[day]!!.size == 1) {
                schedulePresenterCallback.fillDay(selectedDayIndexes[i])
            }
        }
    }

    fun removeFromSchedule(
        days: List<Int>,
        time: String,
        schedulePresenterCallback: SchedulePresenterCallback
    ) {
        for ((i, day) in days.withIndex()) {
            schedule[day]!!.remove(time)

            if (schedule[day]!!.isEmpty()) {
                schedulePresenterCallback.clearDay(selectedDayIndexes[i])
            }
        }
    }
}