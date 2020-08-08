package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.sessions.SessionsInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.sessions.SessionsPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.WorkingDay
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.SessionsView

@InjectViewState
class SessionsPresenter(private val sessionsInteractor: SessionsInteractor) :
    MvpPresenter<SessionsView>(), SessionsPresenterCallback {

    fun getSchedule() {
        sessionsInteractor.getSchedule(this)
    }

    override fun showDays(days: List<WorkingDay>) {
        val sortedDays = days.sortedBy {
            it.getDateForComparison()
        }
        viewState.showDays(sortedDays)
    }

    fun getSessions(day: WorkingDay) {
        clearSessions()
        val sortedSessionList = sessionsInteractor.getSessions(day).sortedBy {
            it.startTime
        }
        viewState.showTime(sortedSessionList)
    }

    fun clearSessions() {
        sessionsInteractor.selectedTime = ""
        viewState.clearSessionsLayout()
    }

    fun updateTime(time: String) {
        sessionsInteractor.updateTime(time, this)
    }

    override fun clearTime(time: String) {
        viewState.clearTime(time)
    }

    override fun selectTime(selectedTime: String) {
        viewState.selectTime(selectedTime)
    }

}