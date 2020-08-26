package com.bunbeauty.ideal.myapplication.clean_architecture.business.chat.i_chat.message.i_message

import android.util.Log
import com.bunbeauty.ideal.myapplication.clean_architecture.Tag
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.chat.MessagesPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.schedule.UpdateScheduleCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Order
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule.ScheduleWithWorkingTime
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.IScheduleRepository

class MessagesScheduleInteractor(private val scheduleRepository: IScheduleRepository) :
    IMessageScheduleInteractor, UpdateScheduleCallback {

    private lateinit var messagesPresenterCallback: MessagesPresenterCallback

    override fun deleteOrderFromSchedule(
        order: Order,
        messagesPresenterCallback: MessagesPresenterCallback
    ) {
        this.messagesPresenterCallback = messagesPresenterCallback
        scheduleRepository.updateScheduleRemoveOrders(order, this)
    }

    override fun returnUpdatedCallback(obj: ScheduleWithWorkingTime) {
        Log.d(Tag.TEST_TAG, "Updated schedule $obj")
        messagesPresenterCallback.sendMessage("Отказ от исполнения услуги.")
    }

}