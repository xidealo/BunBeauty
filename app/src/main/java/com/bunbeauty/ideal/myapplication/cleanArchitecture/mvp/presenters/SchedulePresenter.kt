package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.schedule.ScheduleInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.ScheduleView

@InjectViewState
class SchedulePresenter(private val scheduleInteractor: ScheduleInteractor) :
    MvpPresenter<ScheduleView>() {

    fun getDateString(dayIndex: Int): String {
        return scheduleInteractor.getDateString(dayIndex)
    }

    fun getTineString(timeIndex: Int): String {
        return scheduleInteractor.getTineString(timeIndex)
    }

    fun isPastDay(dayIndex: Int): Boolean {
        return scheduleInteractor.isPastDay(dayIndex)
    }
}