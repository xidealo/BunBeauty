package com.bunbeauty.ideal.myapplication.clean_architecture.domain.chat.i_chat.message.i_message

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.chat.MessagesPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message

interface IMessagesMessageInteractor {

    fun getMessages(
        dialog: Dialog,
        loadingLimit: Int,
        messagesPresenterCallback: MessagesPresenterCallback
    )

    fun removeObservers()

    fun cancelOrder(
        message: Message,
        dialog: Dialog,
        messagesPresenterCallback: MessagesPresenterCallback
    )

    fun setIsSmoothScrollingToPosition(isSmoothScrollingToPosition: Boolean)
    fun getMyMessagesLink(): List<Message>
    fun updateMessages(message: Message, messagesPresenterCallback: MessagesPresenterCallback)
    fun sendMessage(message: Message, messagesPresenterCallback: MessagesPresenterCallback)

    fun getId(userId: String, dialogId: String): String
}