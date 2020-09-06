package com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.schedule.DeleteScheduleCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.schedule.GetScheduleCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.schedule.InsertScheduleCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.schedule.UpdateScheduleCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Order
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule.ScheduleWithWorkingTime

interface IScheduleRepository {
    fun getScheduleByUserId(userId: String, getScheduleCallback: GetScheduleCallback)
    fun deleteSchedule(scheduleWithWorkingTime: ScheduleWithWorkingTime, deleteScheduleCallback: DeleteScheduleCallback)
    fun insertSchedule(scheduleWithWorkingTime: ScheduleWithWorkingTime, insertScheduleCallback: InsertScheduleCallback)
    fun updateScheduleRemoveOrders(order: Order, updateScheduleCallback: UpdateScheduleCallback)
}