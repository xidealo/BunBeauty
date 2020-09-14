package com.bunbeauty.ideal.myapplication.clean_architecture.domain.chat.i_chat.message

import android.util.Log
import com.bunbeauty.ideal.myapplication.clean_architecture.Tag
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.chat.i_chat.message.i_message.IMessagesOrderInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.chat.MessagesPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.order.DeleteOrderCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.order.OrderCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Order
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.IOrderRepository

class MessagesOrderInteractor(private val orderRepository: IOrderRepository) :
    IMessagesOrderInteractor, OrderCallback, DeleteOrderCallback {

    private lateinit var messagesPresenterCallback: MessagesPresenterCallback

    override fun deleteOrder(
        message: Message,
        messagesPresenterCallback: MessagesPresenterCallback
    ) {
        this.messagesPresenterCallback = messagesPresenterCallback
        orderRepository.getById(message.userId, message.orderId, this)
    }

    override fun returnGottenObject(obj: Order?) {
        if (obj == null) return
        Log.d(Tag.TEST_TAG, "returnGottenObject $obj")
        orderRepository.delete(obj, this)
    }

    override fun returnDeletedCallback(obj: Order) {
        messagesPresenterCallback.deleteOrderFromSchedule(obj)
    }
}