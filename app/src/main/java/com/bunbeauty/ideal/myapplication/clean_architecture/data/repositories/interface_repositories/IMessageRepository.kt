package com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.message.*
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message

interface IMessageRepository {
    fun insert(message: Message, insertMessageCallback: InsertMessageCallback)
    fun delete(message: Message, deleteMessageCallback: DeleteMessageCallback)
    fun update(message: Message, updateMessageCallback: UpdateMessageCallback)
    fun get(messagesCallback: MessagesCallback)

    fun getByDialogId(
        dialog: Dialog,
        loadingLimit: Int,
        messagesCallback: MessagesCallback,
        messageCallback: MessageCallback,
        updateMessageCallback: UpdateMessageCallback
    )

    fun deleteByOrderId(
        dialog: Dialog,
        orderId: String,
        deleteAllMessageCallback: DeleteAllMessageCallback
    )

    fun getByIdLastMessage(myId: String, companionId: String, messageCallback: MessageCallback)
}