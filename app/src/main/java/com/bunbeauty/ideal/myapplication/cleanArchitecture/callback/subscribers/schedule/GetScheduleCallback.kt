package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.schedule

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.ScheduleWithDays

interface GetScheduleCallback {
    fun returnGottenSchedule(schedule: ScheduleWithDays)
}