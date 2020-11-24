package com.bunbeauty.ideal.myapplication.clean_architecture.domain.sessions

import android.content.Intent
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.sessions.SessionsPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.schedule.GetScheduleCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.schedule.UpdateScheduleAddOrderCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Order
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule.Schedule
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule.ScheduleWithWorkingTime
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule.Session
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule.WorkingDay
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.IScheduleRepository

class SessionsInteractor(
    private val scheduleRepository: IScheduleRepository
) : GetScheduleCallback, UpdateScheduleAddOrderCallback {

    private lateinit var schedule: ScheduleWithWorkingTime
    private lateinit var sessionsPresenterCallback: SessionsPresenterCallback
    lateinit var service: Service
    var selectedSession: Session? = null
    val sessionList: MutableList<Session> = ArrayList()

    fun getSchedule(intent: Intent, sessionsPresenterCallback: SessionsPresenterCallback) {
        this.sessionsPresenterCallback = sessionsPresenterCallback
        scheduleRepository.getScheduleByUserId(getService(intent).userId, this)
    }

    fun getService(intent: Intent): Service {
        service = intent.getParcelableExtra<Service>(Service.SERVICE) as Service
        return service
    }

    override fun returnGottenObject(gottenSchedule: ScheduleWithWorkingTime?) {
        schedule = gottenSchedule!!.getFutureSchedule()

        val availableDaySet = schedule.getAvailableDays(service.duration)
        if (availableDaySet.isEmpty()) {
            sessionsPresenterCallback.showNoAvailableSessions()
        } else {
            sessionsPresenterCallback.showDays(availableDaySet)
        }
    }

    fun getSessions(workingDay: WorkingDay): List<Session> {
        sessionList.clear()
        sessionList.addAll(schedule.getSessions(service.duration, workingDay.dayOfMonth))
        return sessionList
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

    fun updateSchedule(order: Order, sessionsPresenterCallback: SessionsPresenterCallback) {
        this.sessionsPresenterCallback = sessionsPresenterCallback

        val workingTimeList = schedule.workingTimeList.filter {
            it.time in order.session.startTime until order.session.finishTime
        }
        for (time in workingTimeList) {
            time.orderId = order.id
            time.clientId = order.clientId
        }
        val updatedSchedule = ScheduleWithWorkingTime(
            schedule = Schedule(masterId = schedule.schedule.masterId),
            workingTimeList = ArrayList(workingTimeList)
        )
        scheduleRepository.updateScheduleAddOrder(updatedSchedule, this)
    }

    override fun returnUpdatedScheduleAddOrderCallback(schedule: ScheduleWithWorkingTime) {
    }
}