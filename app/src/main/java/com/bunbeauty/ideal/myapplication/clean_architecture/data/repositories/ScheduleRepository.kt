package com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.schedule.GetScheduleCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.schedule.UpdateScheduleCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.api.ScheduleFirebase
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Order
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule.ScheduleWithWorkingTime
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.IScheduleRepository

class ScheduleRepository(private val scheduleFirebase: ScheduleFirebase) : IScheduleRepository {

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

    override fun updateScheduleRemoveOrders(
        order: Order,
        updateScheduleCallback: UpdateScheduleCallback
    ) {
        scheduleFirebase.updateScheduleRemoveOrders(order, updateScheduleCallback)
    }
}