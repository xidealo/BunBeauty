package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.schedule.GetScheduleCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.schedule.UpdateScheduleCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.ScheduleFirebase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.ScheduleWithWorkingTime
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IScheduleRepository

class ScheduleRepository(private val scheduleFirebase: ScheduleFirebase): IScheduleRepository {

    override fun getScheduleByUserId(userId: String, getScheduleCallback: GetScheduleCallback) {
        scheduleFirebase.getByMasterId(userId, getScheduleCallback)
    }

    override fun updateSchedule(
        scheduleWithWorkingTime: ScheduleWithWorkingTime,
        updateScheduleCallback: UpdateScheduleCallback
    ) {
        scheduleFirebase.update(scheduleWithWorkingTime)
        updateScheduleCallback.returnUpdatedCallback(scheduleWithWorkingTime)
    }
}