package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.sessions.SessionsInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.SessionsView

@InjectViewState
class SessionsPresenter(sessionsInteractor: SessionsInteractor): MvpPresenter<SessionsView>() {
    fun getSchedule() {
        TODO("Not yet implemented")
    }

    fun getDateString(i: Int): String {
        TODO("Not yet implemented")
    }

    fun isPastDay(dayIndex: Int): Boolean {
        TODO("Not yet implemented")
    }

    fun getDayIndex(date: String): Int {
        TODO("Not yet implemented")
    }

    fun isDaySelected(buttonIndex: Int): Boolean {
        TODO("Not yet implemented")
    }

    fun clearSelectedDay(buttonIndex: Int) {
        TODO("Not yet implemented")
    }

    fun getSelectedDay(): Int {
        TODO("Not yet implemented")
    }

    fun setSelectedDay(buttonIndex: Int) {
        TODO("Not yet implemented")
    }

}