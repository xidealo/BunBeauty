package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.schedule

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.schedule.SchedulePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.schedule.UpdateScheduleCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.Schedule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.ScheduleWithDays
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.WorkingTime
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IScheduleRepository
import org.joda.time.DateTime

class ScheduleInteractor(private val scheduleRepository: IScheduleRepository) :
    UpdateScheduleCallback {

    var selectedDayIndexes = ArrayList<Int>()
    var selectedDays = ArrayList<Int>()
    private val schedule = ScheduleWithDays()

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
            if (!schedule.containsWorkingDay(day)) {
                continue
            }

            for (workingTime in schedule.getWorkingTimes(day)) {
                if (isAccurate(workingTime)) {
                    accurateTime.add(workingTime.time)
                } else {
                    inaccurateTime.add(workingTime.time)
                }
            }
        }

        schedulePresenterCallback.showAccurateTime(accurateTime)
        schedulePresenterCallback.showInaccurateTime(inaccurateTime)
    }

    private fun isAccurate(time: WorkingTime): Boolean {
        for (day in selectedDays) {
            if (!schedule.containsWorkingDay(day)) {
                return false
            }

            if (!schedule.getWorkingDay(day)!!.containsWorkingTime(time)) {
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
            if (schedule.containsWorkingDay(day)) {
                schedule.getWorkingDay(day)!!.addWorkingTime(time)
            } else {
                schedule.addWorkingDay(day)
            }

            if (schedule.getWorkingDay(day)!!.workingTimes.size == 1) {
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
            schedule.getWorkingDay(day)!!.removeTime(time)

            if (schedule.getWorkingDay(day)!!.isEmpty()) {
                schedulePresenterCallback.clearDay(selectedDayIndexes[i])
            }
        }
    }

    fun saveSchedule() {
        scheduleRepository.updateSchedule(schedule, this)
    }

    override fun returnUpdatedCallback(schedule: ScheduleWithDays) {

    }
}