package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views

import com.arellomobile.mvp.MvpView

interface ScheduleView: MvpView {
    fun fillDay(dayIndex: Int)
    fun clearDay(dayIndex: Int)
    fun showAccurateTime(accurateTime: Set<String>)
    fun showInaccurateTime(inaccurateTime: Set<String>)
}