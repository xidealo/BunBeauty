package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.schedule

interface SchedulePresenterCallback {
    fun fillDay(dayIndex: Int)
    fun clearDay(dayIndex: Int)
    fun showAccurateTime(accurateTime: Set<String>)
    fun showInaccurateTime(inaccurateTime: Set<String>)
}