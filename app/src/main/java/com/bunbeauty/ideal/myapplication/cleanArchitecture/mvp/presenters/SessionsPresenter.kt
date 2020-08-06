package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.sessions.SessionsInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.sessions.SessionsPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.Session
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.WorkingDay
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.SessionsView

@InjectViewState
class SessionsPresenter(private val sessionsInteractor: SessionsInteractor) :
    MvpPresenter<SessionsView>(), SessionsPresenterCallback {

    fun getSchedule() {
        sessionsInteractor.getSchedule(this)
    }

    override fun showDays(days: List<WorkingDay>) {
        viewState.showDays(days)
    }

    fun getSessions(day: WorkingDay) {
        clearSessions()
        viewState.showTime(sessionsInteractor.getSessions(day))
    }

    fun clearSessions() {
        viewState.clearSessionsLayout()
    }

}