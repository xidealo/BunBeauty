package com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.schedule

import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule.ScheduleWithWorkingTime

interface UpdateScheduleRemoveOrderCallback {
    fun returnUpdatedScheduleRemoveOrderCallback(schedule: ScheduleWithWorkingTime)
}