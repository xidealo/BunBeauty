package com.bunbeauty.ideal.myapplication.clean_architecture.business.chat.i_chat.message

import com.bunbeauty.ideal.myapplication.clean_architecture.business.chat.i_chat.message.i_message.IMessagesOrderInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.chat.MessagesPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.IOrderRepository

class MessagesOrderInteractor(private val orderRepository: IOrderRepository) :
    IMessagesOrderInteractor {

    override fun cancelOrder(
        message: Message,
        messagesPresenterCallback: MessagesPresenterCallback
    ) {

    }
}