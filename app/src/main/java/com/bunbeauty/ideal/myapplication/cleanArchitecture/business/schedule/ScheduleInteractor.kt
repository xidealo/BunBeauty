package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.schedule

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.schedule.SchedulePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.schedule.GetScheduleCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.schedule.UpdateScheduleCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.ScheduleWithDays
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.WorkingTime
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IScheduleRepository
import org.joda.time.DateTime
import org.joda.time.Days
import org.joda.time.format.DateTimeFormat

class ScheduleInteractor(private val scheduleRepository: IScheduleRepository) :
    GetScheduleCallback, UpdateScheduleCallback {

    private lateinit var schedulePresenterCallback: SchedulePresenterCallback

    var selectedDayIndexes = ArrayList<Int>()
    var selectedDays = ArrayList<Int>()
    private var schedule = ScheduleWithDays()

    fun getSchedule(schedulePresenterCallback: SchedulePresenterCallback) {
        this.schedulePresenterCallback = schedulePresenterCallback

        scheduleRepository.getScheduleByUserId(User.getMyId(), this)
    }

    override fun returnGotSchedule(schedule: ScheduleWithDays) {
        this.schedule = schedule
        schedulePresenterCallback.setSchedule(schedule)
    }

    fun getDateString(dayIndex: Int): String {
        val date = getLastMondayDate().plusDays(dayIndex).dayOfMonth

        return date.toString()
    }

    private fun getLastMondayDate(): DateTime {
        val dayOfWeek = DateTime.now().dayOfWeek - 1
        return DateTime.now().minusDays(dayOfWeek)
    }

    fun getDayIndex(date: String): Int {
        return getDaysBetween(getLastMondayDate(), getDateFromString(date))
    }

    fun getDaysBetween(startDate: DateTime, endDate:DateTime): Int {
        return Days.daysBetween(startDate.toLocalDate(), endDate.toLocalDate()).days
    }

    fun getDateFromString(date: String): DateTime {
        return DateTime.parse(date, DateTimeFormat.forPattern("dd-MM-yyyy"))
    }

    fun getTimeString(timeIndex: Int): String {
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
        schedule.schedule.userId = User.getMyId()
        schedule.workingDays.removeAll(schedule.workingDays.filter { it.workingTimes.isEmpty() })
        scheduleRepository.updateSchedule(schedule, this)
    }

    override fun returnUpdatedCallback(schedule: ScheduleWithDays) {
    }
}