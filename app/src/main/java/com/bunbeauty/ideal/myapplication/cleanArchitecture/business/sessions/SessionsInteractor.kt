package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.sessions

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.sessions.SessionsPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.schedule.GetScheduleCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.schedule.UpdateScheduleCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Order
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service.Companion.SERVICE
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.ScheduleWithDays
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.Session
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.WorkingDay
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IScheduleRepository
import org.joda.time.DateTime

class SessionsInteractor(
    private val scheduleRepository: IScheduleRepository,
    private val intent: Intent
) : GetScheduleCallback, UpdateScheduleCallback {

    private lateinit var schedule: ScheduleWithDays
    private lateinit var sessionsPresenterCallback: SessionsPresenterCallback

    var selectedSession: Session? = null
    val sessionList: MutableList<Session> = ArrayList()

    fun getSchedule(sessionsPresenterCallback: SessionsPresenterCallback) {
        this.sessionsPresenterCallback = sessionsPresenterCallback

        scheduleRepository.getScheduleByUserId(getService().userId, this)
    }

    override fun returnGottenSchedule(schedule: ScheduleWithDays) {
        this.schedule = schedule
        sessionsPresenterCallback.showDays(getAvailableDays(getService().duration, schedule))
    }

    fun getAvailableDays(serviceDuration: Float, schedule: ScheduleWithDays): List<WorkingDay> {
        return schedule.workingDays
            .filter { it.isAvailable(serviceDuration) }
            .map { it.workingDay }
    }

    fun getSessions(day: WorkingDay): List<Session> {
        val workingDay = schedule.workingDays.find { it.workingDay == day }!!
        sessionList.addAll(workingDay.getSessions(getService().duration))

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

    fun getService(): Service {
        return intent.getSerializableExtra(SERVICE) as Service
    }

    fun updateSchedule(sessionsPresenterCallback: SessionsPresenterCallback, order: Order) {
        val day = DateTime(order.session.startTime).dayOfMonth
        val timeList = schedule.getWorkingDay(day)!!.workingTimes.filter {
            it.time in order.session.startTime until order.session.finishTime
        }
        for (time in timeList) {
            time.orderId = order.id
        }
        scheduleRepository.updateSchedule(schedule, this)
    }

    override fun returnUpdatedCallback(obj: ScheduleWithDays) {

    }
}