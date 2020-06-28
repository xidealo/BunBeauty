package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.schedule.UpdateScheduleCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.ScheduleFirebase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.Schedule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.ScheduleWithDays
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IScheduleRepository

class ScheduleRepository(private val scheduleFirebase: ScheduleFirebase): IScheduleRepository {

    override fun updateSchedule(
        schedule: ScheduleWithDays,
        updateScheduleCallback: UpdateScheduleCallback
    ) {
        scheduleFirebase.update(schedule)
        updateScheduleCallback.returnUpdatedCallback(schedule)
    }
}