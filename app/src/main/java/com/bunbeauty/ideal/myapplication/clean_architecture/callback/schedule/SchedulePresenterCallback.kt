package com.bunbeauty.ideal.myapplication.clean_architecture.callback.schedule

import java.util.LinkedHashSet

interface SchedulePresenterCallback {

    fun showSchedule(dayIndexes: Set<Int>)

    fun fillDay(dayIndex: Int)
    fun clearDay(dayIndex: Int)

    fun showAccurateTime(accurateTimeSet: Set<String>)
    fun showTimeWithOrder(timeWithOrderSet: Set<String>)
    fun showInaccurateTime(inaccurateTimeSet: Set<String>)
    fun clearTime(timeString: String)

    fun showScheduleSaved()
}