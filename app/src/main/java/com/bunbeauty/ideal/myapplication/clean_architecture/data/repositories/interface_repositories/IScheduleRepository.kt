package com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.schedule.GetScheduleCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.schedule.UpdateScheduleCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Order
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule.ScheduleWithWorkingTime

interface IScheduleRepository {
    fun getScheduleByUserId(userId: String, getScheduleCallback: GetScheduleCallback)
    fun updateSchedule(scheduleWithWorkingTime: ScheduleWithWorkingTime, updateScheduleCallback: UpdateScheduleCallback)
    fun updateScheduleRemoveOrders(order: Order, updateScheduleCallback: UpdateScheduleCallback)
}