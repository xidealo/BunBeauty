package com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.schedule.*
import com.bunbeauty.ideal.myapplication.clean_architecture.data.api.ScheduleFirebase
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Order
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule.ScheduleWithWorkingTime
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.IScheduleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScheduleRepository(private val scheduleFirebase: ScheduleFirebase) : BaseRepository(),
    IScheduleRepository {

    override fun getScheduleByUserId(userId: String, getScheduleCallback: GetScheduleCallback) {
        launch {
            scheduleFirebase.getByMasterId(userId, getScheduleCallback)
        }
    }

    override fun insertSchedule(
        scheduleWithWorkingTime: ScheduleWithWorkingTime,
        insertScheduleCallback: InsertScheduleCallback
    ) {
        launch {
            scheduleFirebase.insert(scheduleWithWorkingTime)
            withContext(Dispatchers.Main) {
                insertScheduleCallback.returnCreatedCallback(scheduleWithWorkingTime)
            }
        }
    }

    override fun deleteSchedule(
        scheduleWithWorkingTime: ScheduleWithWorkingTime,
        deleteScheduleCallback: DeleteScheduleCallback
    ) {
        launch {
            scheduleFirebase.delete(scheduleWithWorkingTime)
            withContext(Dispatchers.Main) {
                deleteScheduleCallback.returnDeletedCallback(scheduleWithWorkingTime)
            }
        }
    }

    override fun updateScheduleAddOrder(
        schedule: ScheduleWithWorkingTime,
        updateScheduleAddOrderCallback: UpdateScheduleAddOrderCallback
    ) {
        launch {
            scheduleFirebase.updateScheduleAddOrder(schedule, updateScheduleAddOrderCallback)
        }
    }

    override fun updateScheduleRemoveOrder(
        order: Order,
        updateScheduleRemoveOrderCallback: UpdateScheduleRemoveOrderCallback
    ) {
        scheduleFirebase.updateScheduleRemoveOrder(order, updateScheduleRemoveOrderCallback)
    }
}