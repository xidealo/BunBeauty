package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.schedule

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.ScheduleWithDays

interface SchedulePresenterCallback {
    fun fillDay(dayIndex: Int)
    fun clearDay(dayIndex: Int)
    fun showAccurateTime(accurateTime: Set<String>)
    fun showInaccurateTime(inaccurateTime: Set<String>)
    fun setSchedule(schedule: ScheduleWithDays)
}