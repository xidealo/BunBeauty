package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.sessions

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.sessions.SessionsPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.schedule.GetScheduleCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service.Companion.DURATION
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.ScheduleWithDays
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.Session
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.WorkingDay
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IScheduleRepository

class SessionsInteractor(
    private val scheduleRepository: IScheduleRepository,
    private val intent: Intent
) : GetScheduleCallback {

    lateinit var schedule: ScheduleWithDays
    private lateinit var sessionsPresenterCallback: SessionsPresenterCallback

    fun getSchedule(sessionsPresenterCallback: SessionsPresenterCallback) {
        this.sessionsPresenterCallback = sessionsPresenterCallback

        scheduleRepository.getScheduleByUserId(getMasterId(), this)
    }

    private fun getMasterId(): String {
        return intent.getStringExtra(User.USER_ID)!!
    }

    override fun returnGotSchedule(schedule: ScheduleWithDays) {
        this.schedule = schedule
        sessionsPresenterCallback.showDays(getAvailableDays(getServiceDuration(), schedule))
    }

    private fun getServiceDuration(): Float {
        return intent.getFloatExtra(DURATION, 0.5f)
    }

    fun getAvailableDays(serviceDuration: Float, schedule: ScheduleWithDays): List<WorkingDay> {
        return schedule.workingDays
            .filter { it.isAvailable(serviceDuration) }
            .map { it.workingDay }
    }

    fun getSessions(day: WorkingDay): List<Session> {
        val workingDay = schedule.workingDays.find { it.workingDay.date == day.date }!!
        return workingDay.getSessions(getServiceDuration())
    }

    var selectedTime = ""

    fun updateTime(time: String, sessionsPresenterCallback: SessionsPresenterCallback) {
        if (time == selectedTime) {
            selectedTime = ""
            sessionsPresenterCallback.clearTime(time)
        } else {
            if (selectedTime.isNotEmpty()) {
                sessionsPresenterCallback.clearTime(selectedTime)
            }
            sessionsPresenterCallback.selectTime(time)

            selectedTime = time
        }
    }
}