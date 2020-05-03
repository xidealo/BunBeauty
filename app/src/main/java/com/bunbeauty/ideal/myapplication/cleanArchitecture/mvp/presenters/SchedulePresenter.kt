package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.schedule.ScheduleInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.schedule.SchedulePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.ScheduleView

@InjectViewState
class SchedulePresenter(private val scheduleInteractor: ScheduleInteractor) :
    MvpPresenter<ScheduleView>(), SchedulePresenterCallback {

    fun getDateString(dayIndex: Int): String {
        return scheduleInteractor.getDateString(dayIndex)
    }

    fun getTineString(timeIndex: Int): String {
        return scheduleInteractor.getTineString(timeIndex)
    }

    fun isPastDay(dayIndex: Int): Boolean {
        return scheduleInteractor.isPastDay(dayIndex)
    }

    fun rememberDay(dayIndex: Int, day: String) {
        scheduleInteractor.selectedDayIndexes.add(dayIndex)
        scheduleInteractor.selectedDays.add(day.toInt())
        scheduleInteractor.getTime(this)
    }

    override fun showAccurateTime(accurateTime: Set<String>) {
        viewState.showAccurateTime(accurateTime)
    }

    override fun showInaccurateTime(inaccurateTime: Set<String>) {
        viewState.showInaccurateTime(inaccurateTime)
    }

    fun getSelectedDays(): List<Int> {
        return scheduleInteractor.selectedDayIndexes
    }

    fun forgotAllDays() {
        scheduleInteractor.selectedDayIndexes.clear()
        scheduleInteractor.selectedDays.clear()
    }

    fun addToSchedule(days: List<String>, time: String) {
        scheduleInteractor.addToSchedule(days.map { it.toInt() }, time, this)
    }

    override fun fillDay(dayIndex: Int) {
        viewState.fillDay(dayIndex)
    }

    fun removeFromSchedule(days: List<String>, time: String) {
        scheduleInteractor.removeFromSchedule(days.map { it.toInt() }, time, this)
    }

    override fun clearDay(dayIndex: Int) {
        viewState.clearDay(dayIndex)
    }
}