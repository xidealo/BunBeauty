package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.sessions

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.sessions.SessionsPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.schedule.GetScheduleCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.schedule.UpdateScheduleCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Order
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service.Companion.SERVICE
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.ScheduleWithWorkingTime
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.Session
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.WorkingDay
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IScheduleRepository

class SessionsInteractor(
    private val scheduleRepository: IScheduleRepository,
    private val intent: Intent
) : GetScheduleCallback, UpdateScheduleCallback {

    private lateinit var schedule: ScheduleWithWorkingTime
    private lateinit var sessionsPresenterCallback: SessionsPresenterCallback

    var selectedSession: Session? = null
    val sessionList: MutableList<Session> = ArrayList()

    fun getSchedule(sessionsPresenterCallback: SessionsPresenterCallback) {
        this.sessionsPresenterCallback = sessionsPresenterCallback

        scheduleRepository.getScheduleByUserId(getService().userId, this)
    }

    override fun returnGottenObject(schedule: ScheduleWithWorkingTime?) {
        this.schedule = schedule!!
        sessionsPresenterCallback.showDays(schedule.getAvailableDays(getService().duration))
    }

    fun getSessions(workingDay: WorkingDay): List<Session> {
        return schedule.getSessions(getService().duration, workingDay.dayOfMonth)
    }

    fun updateTime(time: String, sessionsPresenterCallback: SessionsPresenterCallback) {
        if (time == selectedSession?.getStringStartTime()) {
            clearSelectedSession(sessionsPresenterCallback)
        } else {
            clearSelectedSession(sessionsPresenterCallback)
            sessionsPresenterCallback.selectTime(time)
            sessionsPresenterCallback.enableMakeAppointmentButton()

            selectedSession = sessionList.find { it.getStringStartTime() == time }
        }
    }

    fun clearSelectedSession(sessionsPresenterCallback: SessionsPresenterCallback) {
        if (selectedSession == null) {
            return
        }

        sessionsPresenterCallback.disableMakeAppointmentButton()
        sessionsPresenterCallback.clearTime(selectedSession!!.getStringStartTime())
        selectedSession = null
    }

    fun getService(): Service {
        return intent.getSerializableExtra(SERVICE) as Service
    }

    fun updateSchedule(order: Order, sessionsPresenterCallback: SessionsPresenterCallback) {
        val timeList = schedule.workingTimeList.filter {
            it.time in order.session.startTime until order.session.finishTime
        }
        for (time in timeList) {
            time.orderId = order.id
            time.clientId = order.clientId
        }
        scheduleRepository.updateSchedule(schedule, this)
    }

    override fun returnUpdatedCallback(obj: ScheduleWithWorkingTime) {}
}