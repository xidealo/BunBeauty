package com.bunbeauty.ideal.myapplication.clean_architecture.domain.chat.i_chat.message

import android.util.Log
import com.bunbeauty.ideal.myapplication.clean_architecture.Tag
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.chat.i_chat.message.i_message.IMessagesMessageInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.chat.MessagesPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.message.*
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.MessageRepository

class MessagesMessageInteractor(private val messageRepository: MessageRepository) :
    IMessagesMessageInteractor, InsertMessageCallback, MessageCallback, MessagesCallback,
    UpdateMessageCallback, DeleteAllMessageCallback {

    private var cacheMessages = mutableListOf<Message>()
    private lateinit var messagesPresenterCallback: MessagesPresenterCallback
    override var isSmoothScrollingToPosition = true

    override fun getMyMessagesLink() = cacheMessages

    override fun getMessages(
        dialog: Dialog,
        loadingLimit: Int,
        messagesPresenterCallback: MessagesPresenterCallback
    ) {
        this.messagesPresenterCallback = messagesPresenterCallback
        messageRepository.getByDialogId(dialog, loadingLimit, this, this, this)
    }

    override fun cancelOrder(
        message: Message,
        dialog: Dialog,
        messagesPresenterCallback: MessagesPresenterCallback
    ) {
        this.messagesPresenterCallback = messagesPresenterCallback
        messageRepository.deleteByOrderId(dialog, message.orderId, this)
    }

    override fun returnDeletedList(objects: List<Message>) {
        if (objects.isNotEmpty()) {
            messagesPresenterCallback.deleteOrder(objects.first())

            for (message in objects) {
                cacheMessages.remove(message)
                messagesPresenterCallback.removeMessageAdapter(message)
            }
        }
    }

    override fun returnList(objects: List<Message>) {
        if (objects.isEmpty()) {
            messagesPresenterCallback.showEmptyScreen()
        }
    }

    override fun updateMessages(
        message: Message,
        messagesPresenterCallback: MessagesPresenterCallback
    ) {
        cacheMessages.remove(cacheMessages.find { it.id == message.id }!!)
        cacheMessages.add(message)
        messagesPresenterCallback.updateMessageAdapter(message)
    }

    override fun sendMessage(
        message: Message,
        messagesPresenterCallback: MessagesPresenterCallback
    ) {
        this.messagesPresenterCallback = messagesPresenterCallback
        if (message.message.isNotEmpty()) {
            messageRepository.insert(message, this)
        }
    }

    /**
     * sent message and update unchecked dialog to our companion
     */
    override fun returnCreatedCallback(obj: Message) {
        messagesPresenterCallback.updateUncheckedDialog(obj)
    }

    /**
     * add text message or if it is my message add also commentMessage
     */
    override fun returnGottenObject(obj: Message?) {
        if (obj == null) return

        if (obj.type == Message.TEXT_STATUS || obj.ownerId == User.getMyId()) {
            addMessage(obj)
        } else {
            Log.d(Tag.TEST_TAG, "Тип сообщения ${obj.type} user id ${obj.userId}")
        }
    }

    private fun addMessage(message: Message) {
        //only from my dialog
        if (message.dialogId == User.getMyId()) {
            cacheMessages.add(message)
            messagesPresenterCallback.setUnchecked()
            messagesPresenterCallback.showMessage(message, isSmoothScrollingToPosition)
        } else {
            Log.d(Tag.TEST_TAG, "Сообщение из другого диалога $message")
        }
    }

    /**
     * wrote comment then update our message to [Message.TEXT_MESSAGE_STATUS] and show it
     */
    override fun returnUpdatedCallback(obj: Message) {
        if (cacheMessages.find { it.id == obj.id } == null) {
            cacheMessages.add(obj)
            messagesPresenterCallback.showMessage(obj, isSmoothScrollingToPosition)
        }
    }

    override fun getId(userId: String, dialogId: String): String {
        return messageRepository.getIdForNew(userId, dialogId)
    }
}