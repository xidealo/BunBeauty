package com.bunbeauty.ideal.myapplication.clean_architecture.business.chat.i_chat.message.i_message

import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Order
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.IScheduleRepository

class MessagesScheduleInteractor(private val scheduleRepository: IScheduleRepository) :
    IMessageScheduleInteractor {

    override fun deleteOrderFromSchedule(order: Order) {

    }

}