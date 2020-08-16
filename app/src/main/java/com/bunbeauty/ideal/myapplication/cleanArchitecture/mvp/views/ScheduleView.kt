package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views

import com.arellomobile.mvp.MvpView
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.ScheduleWithWorkingTime

interface ScheduleView: MvpView {
    fun fillDay(dayIndex: Int)
    fun clearDay(dayIndex: Int)
    fun showAccurateTime(accurateTime: Set<String>)
    fun showInaccurateTime(inaccurateTime: Set<String>)
    fun showSchedule(dayIndexes: Set<Int>)
    fun showMessage(message: String)
    fun goBack()
}