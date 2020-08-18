package com.bunbeauty.ideal.myapplication.clean_architecture.callback.schedule

interface SchedulePresenterCallback {
    fun fillDay(dayIndex: Int)
    fun clearDay(dayIndex: Int)
    fun showAccurateTime(accurateTime: Set<String>)
    fun showInaccurateTime(inaccurateTime: Set<String>)
    fun showSchedule(dayIndexes: Set<Int>)
    fun showScheduleSaved()
}