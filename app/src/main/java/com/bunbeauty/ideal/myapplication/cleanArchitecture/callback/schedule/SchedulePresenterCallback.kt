package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.schedule

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.Schedule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.ScheduleWithWorkingTime
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.WorkingTime

interface SchedulePresenterCallback {
    fun fillDay(dayIndex: Int)
    fun clearDay(dayIndex: Int)
    fun showAccurateTime(accurateTime: Set<String>)
    fun showInaccurateTime(inaccurateTime: Set<String>)
    fun showSchedule(dayIndexes: Set<Int>)
    fun showScheduleSaved()
}