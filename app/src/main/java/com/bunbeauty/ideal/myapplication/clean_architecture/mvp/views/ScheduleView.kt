package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views

import com.arellomobile.mvp.MvpView

interface ScheduleView: MvpView {

    fun fillDay(dayIndex: Int)
    fun clearDay(dayIndex: Int)

    fun showAccurateTime(accurateTimeSet: Set<String>)
    fun showTimeWithOrder(timeWithOrderSet: Set<String>)
    fun showInaccurateTime(inaccurateTimeSet: Set<String>)
    fun clearTime(timeString: String)

    fun showSchedule(dayIndexes: Set<Int>)
    fun showMessage(message: String)
    fun goBack()
}