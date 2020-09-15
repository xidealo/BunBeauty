package com.bunbeauty.ideal.myapplication.clean_architecture.domain.schedule

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.schedule.SchedulePresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.schedule.DeleteScheduleCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.schedule.GetScheduleCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.schedule.InsertScheduleCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule.Schedule.Companion.MONTHS
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule.ScheduleWithWorkingTime
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule.WorkingTime
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.IScheduleRepository
import org.joda.time.DateTime
import org.joda.time.Days

class ScheduleInteractor(private val scheduleRepository: IScheduleRepository) :
    GetScheduleCallback, InsertScheduleCallback, DeleteScheduleCallback {

    private lateinit var schedulePresenterCallback: SchedulePresenterCallback

    var selectedDayIndexes = ArrayList<Int>()
    var selectedDays = ArrayList<Int>()

    private lateinit var schedule: ScheduleWithWorkingTime
    private lateinit var deletedSchedule: ScheduleWithWorkingTime
    private val addedSchedule = ScheduleWithWorkingTime()

    fun getSchedule(schedulePresenterCallback: SchedulePresenterCallback) {
        this.schedulePresenterCallback = schedulePresenterCallback

        scheduleRepository.getScheduleByUserId(User.getMyId(), this)
    }

    override fun returnGottenObject(gottenSchedule: ScheduleWithWorkingTime?) {
        schedule = gottenSchedule!!.getFutureSchedule()
        deletedSchedule = gottenSchedule.getPastSchedule()
        addedSchedule.schedule.masterId = gottenSchedule.schedule.masterId

        schedulePresenterCallback.showSchedule(getDayIndexes(schedule.workingTimeList))
    }

    fun getDayIndexes(workingTimeList: List<WorkingTime>): Set<Int> {
        return workingTimeList.map {
            getDaysBetween(getLastMondayDate(), DateTime(it.time))
        }.toSet()
    }

    fun getDaysBetween(startDate: DateTime, endDate: DateTime): Int {
        return Days.daysBetween(startDate.toLocalDate(), endDate.toLocalDate()).days
    }

    fun getStringDate(dayIndex: Int): String {
        val day = getLastMondayDate().plusDays(dayIndex).dayOfMonth.toString()
        val monthNumber = getLastMondayDate().plusDays(dayIndex).monthOfYear

        return "$day$DAY_MONTH_DELIMITER${MONTHS[monthNumber]}"
    }

    fun getLastMondayDate(): DateTime {
        val gettingDateTime = DateTime(schedule.schedule.gettingTime)
        val dayOfWeek = gettingDateTime.dayOfWeek - 1
        return gettingDateTime.minusDays(dayOfWeek)
    }

    fun isPastDay(dayIndex: Int): Boolean {
        val dayOfWeek = DateTime(schedule.schedule.gettingTime).dayOfWeek - 1

        return dayIndex < dayOfWeek
    }

    fun getDayFromString(dayString: String): Int {
        return dayString.split(DAY_MONTH_DELIMITER).first().toInt()
    }

    fun getTime(schedulePresenterCallback: SchedulePresenterCallback) {
        val accurateTimeSet = LinkedHashSet<String>()
        val timeWithOrderSet = LinkedHashSet<String>()
        val inaccurateTimeSet = LinkedHashSet<String>()

        for (day in selectedDays) {
            if (!schedule.containsWorkingDay(day)) {
                continue
            }

            for (workingTime in schedule.getWorkingTimes(day)) {
                if (isAccurate(workingTime)) {
                    accurateTimeSet.add(workingTime.getStringTime())
                } else {
                    inaccurateTimeSet.add(workingTime.getStringTime())
                }

                if (selectedDays.size == 1 && workingTime.orderId.isNotEmpty()) {
                    timeWithOrderSet.add(workingTime.getStringTime())
                }
            }
        }

        schedulePresenterCallback.showAccurateTime(accurateTimeSet)
        schedulePresenterCallback.showTimeWithOrder(timeWithOrderSet)
        schedulePresenterCallback.showInaccurateTime(inaccurateTimeSet)
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
        days: List<String>,
        timeString: String,
        schedulePresenterCallback: SchedulePresenterCallback
    ) {
        for ((i, dayString) in days.withIndex()) {
            val day = getDayFromString(dayString)
            val workingTime = schedule.addWorkingTime(day, timeString)
            if (deletedSchedule.containsWorkingTime(workingTime)) {
                deletedSchedule.removeWorkingTime(workingTime!!)
            } else {
                addedSchedule.addWorkingTime(workingTime)
            }

            schedulePresenterCallback.fillDay(selectedDayIndexes[i])
        }
    }

    fun deleteFromSchedule(
        stringDays: List<String>,
        timeString: String,
        schedulePresenterCallback: SchedulePresenterCallback
    ) {
        val days = stringDays.map { getDayFromString(it) }
        if (schedule.haveSomeOrder(days, timeString)) {
            schedulePresenterCallback.showInaccurateTime(setOf(timeString))
        } else {
            schedulePresenterCallback.clearTime(timeString)
        }

        for ((i, day) in days.withIndex()) {
            val workingTime = schedule.removeWorkingTime(day, timeString)
            if (addedSchedule.containsWorkingTime(workingTime)) {
                addedSchedule.removeWorkingTime(workingTime!!)
            } else {
                deletedSchedule.addWorkingTime(workingTime)
            }

            if (schedule.getWorkingTimes(day).isEmpty()) {
                schedulePresenterCallback.clearDay(selectedDayIndexes[i])
            }
        }
    }

    fun saveSchedule(schedulePresenterCallback: SchedulePresenterCallback) {
        this.schedulePresenterCallback = schedulePresenterCallback

        scheduleRepository.deleteSchedule(deletedSchedule, this)
        scheduleRepository.insertSchedule(addedSchedule, this)
    }

    override fun returnDeletedCallback(obj: ScheduleWithWorkingTime) {}

    override fun returnCreatedCallback(obj: ScheduleWithWorkingTime) {
        schedulePresenterCallback.showScheduleSaved()
    }

    companion object {
        const val DAY_MONTH_DELIMITER = "\n"
    }
}