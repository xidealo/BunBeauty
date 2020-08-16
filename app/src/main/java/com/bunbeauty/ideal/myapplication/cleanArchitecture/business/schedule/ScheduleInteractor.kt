package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.schedule

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.schedule.SchedulePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.schedule.GetScheduleCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.schedule.UpdateScheduleCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.ScheduleWithWorkingTime
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.WorkingTime
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IScheduleRepository
import org.joda.time.DateTime
import org.joda.time.Days

class ScheduleInteractor(private val scheduleRepository: IScheduleRepository) :
    GetScheduleCallback, UpdateScheduleCallback {

    private lateinit var schedulePresenterCallback: SchedulePresenterCallback

    private val now = DateTime.now()
    var selectedDayIndexes = ArrayList<Int>()
    var selectedDays = ArrayList<Int>()

    private lateinit var schedule: ScheduleWithWorkingTime

    fun getSchedule(schedulePresenterCallback: SchedulePresenterCallback) {
        this.schedulePresenterCallback = schedulePresenterCallback

        scheduleRepository.getScheduleByUserId(User.getMyId(), this)
    }

    override fun returnGottenObject(schedule: ScheduleWithWorkingTime?) {
        this.schedule = schedule!!

        schedulePresenterCallback.showSchedule(getDayIndexes(schedule.workingTimeList))
    }

    fun getDayIndexes(workingTimeList: List<WorkingTime>): Set<Int> {
        return workingTimeList.map {
            getDaysBetween(getLastMondayDate(), DateTime(it.time))
        }.toSet()
    }

    fun getStringDayOfMonth(dayIndex: Int): String {
        return getLastMondayDate().plusDays(dayIndex).dayOfMonth.toString()
    }

    fun getLastMondayDate(): DateTime {
        val dayOfWeek = DateTime.now().dayOfWeek - 1
        return now.minusDays(dayOfWeek)
    }

    fun getDaysBetween(startDate: DateTime, endDate: DateTime): Int {
        return Days.daysBetween(startDate.toLocalDate(), endDate.toLocalDate()).days
    }

    fun isPastDay(dayIndex: Int): Boolean {
        val dayOfWeek = DateTime.now().dayOfWeek - 1

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
                    accurateTime.add(workingTime.getStringTime())
                } else {
                    inaccurateTime.add(workingTime.getStringTime())
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

            if (!schedule.containsWorkingTime(day, time)) {
                return false
            }
        }

        return true
    }

    fun addToSchedule(
        days: List<Int>,
        timeString: String,
        schedulePresenterCallback: SchedulePresenterCallback
    ) {
        for ((i, day) in days.withIndex()) {
            schedule.addWorkingTime(day, timeString)
            schedulePresenterCallback.fillDay(selectedDayIndexes[i])
        }
    }

    fun removeFromSchedule(
        days: List<Int>,
        timeString: String,
        schedulePresenterCallback: SchedulePresenterCallback
    ) {
        for ((i, day) in days.withIndex()) {
            schedule.removeTime(day, timeString)

            if (schedule.getWorkingTimes(day).isEmpty()) {
                schedulePresenterCallback.clearDay(selectedDayIndexes[i])
            }
        }
    }

    fun saveSchedule(schedulePresenterCallback: SchedulePresenterCallback) {
        this.schedulePresenterCallback = schedulePresenterCallback

        schedule.schedule.masterId = User.getMyId()
        scheduleRepository.updateSchedule(schedule, this)
    }

    override fun returnUpdatedCallback(schedule: ScheduleWithWorkingTime) {
        schedulePresenterCallback.showScheduleSaved()
    }
}