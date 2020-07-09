package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.schedule.GetScheduleCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.schedule.UpdateScheduleCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.ScheduleWithDays

interface IScheduleRepository {
    fun getScheduleByUserId(userId: String, getScheduleCallback: GetScheduleCallback)
    fun updateSchedule(scheduleWithDays: ScheduleWithDays, updateScheduleCallback: UpdateScheduleCallback)
}