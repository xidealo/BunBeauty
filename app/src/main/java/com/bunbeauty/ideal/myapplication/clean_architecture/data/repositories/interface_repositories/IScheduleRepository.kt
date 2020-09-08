package com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.schedule.*
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Order
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule.ScheduleWithWorkingTime

interface IScheduleRepository {
    fun getScheduleByUserId(userId: String, getScheduleCallback: GetScheduleCallback)
    fun deleteSchedule(scheduleWithWorkingTime: ScheduleWithWorkingTime, deleteScheduleCallback: DeleteScheduleCallback)
    fun insertSchedule(scheduleWithWorkingTime: ScheduleWithWorkingTime, insertScheduleCallback: InsertScheduleCallback)
    fun updateScheduleAddOrder(schedule: ScheduleWithWorkingTime, updateScheduleAddOrderCallback: UpdateScheduleAddOrderCallback)
    fun updateScheduleRemoveOrder(order: Order, updateScheduleRemoveOrderCallback: UpdateScheduleRemoveOrderCallback)
}