package com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.message.*
import com.bunbeauty.ideal.myapplication.clean_architecture.data.api.MessageFirebase
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.IMessageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MessageRepository(private val messageFirebase: MessageFirebase) : BaseRepository(),
    IMessageRepository {

    override fun insert(message: Message, insertMessageCallback: InsertMessageCallback) {
        launch {
            messageFirebase.insert(message)
            withContext(Dispatchers.Main) {
                insertMessageCallback.returnCreatedCallback(message)
            }
        }
    }

    override fun delete(message: Message, deleteMessageCallback: DeleteMessageCallback) {
        launch {

        }
    }

    override fun update(message: Message, updateMessageCallback: UpdateMessageCallback) {
        launch {
            messageFirebase.update(message)
            withContext(Dispatchers.Main) {
                updateMessageCallback.returnUpdatedCallback(message)
            }
        }
    }

    override fun get(messagesCallback: MessagesCallback) {
        launch {

        }
    }

    override fun getByDialogId(
        dialog: Dialog,
        loadingLimit: Int,
        messagesCallback: MessagesCallback,
        messageCallback: MessageCallback,
        updateMessageCallback: UpdateMessageCallback
    ) {
        launch {
            messageFirebase.getByDialogId(
                dialog,
                loadingLimit,
                messagesCallback,
                messageCallback,
                updateMessageCallback
            )
        }
    }

    override fun deleteByOrderId(
        dialog: Dialog,
        orderId: String,
        deleteAllMessageCallback: DeleteAllMessageCallback
    ) {
        launch {
            messageFirebase.deleteByOrderId(
                dialog,
                orderId,
                deleteAllMessageCallback
            )
        }
    }

    override fun getByIdLastMessage(
        myId: String,
        companionId: String,
        messageCallback: MessageCallback
    ) {
        launch {
            messageFirebase.getLastMessage(
                myId,
                companionId,
                messageCallback
            )
        }
    }

    override fun removeObservers() {
        messageFirebase.removeObservers()
    }

    fun getIdForNew(userId: String, dialogId: String): String = messageFirebase.getIdForNew(
        userId,
        dialogId
    )

}