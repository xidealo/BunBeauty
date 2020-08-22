package com.bunbeauty.ideal.myapplication.clean_architecture.business.chat

import android.util.Log
import com.bunbeauty.ideal.myapplication.clean_architecture.Tag
import com.bunbeauty.ideal.myapplication.clean_architecture.business.chat.i_chat.IMessagesMessageInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.chat.MessagesPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.message.InsertMessageCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.message.MessageCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.message.MessagesCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.message.UpdateMessageCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.MessageRepository

class MessagesMessageInteractor(private val messageRepository: MessageRepository) :
    IMessagesMessageInteractor, InsertMessageCallback, MessageCallback, MessagesCallback,
    UpdateMessageCallback {

    private var cacheMessages = mutableListOf<Message>()
    private lateinit var messagesPresenterCallback: MessagesPresenterCallback

    override fun getMyMessagesLink() = cacheMessages

    override fun getMessages(
        dialog: Dialog,
        loadingLimit: Int,
        messagesPresenterCallback: MessagesPresenterCallback
    ) {
        this.messagesPresenterCallback = messagesPresenterCallback
        messageRepository.getByDialogId(dialog, loadingLimit, this, this, this)
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
    override fun returnGottenObject(element: Message?) {
        if (element == null) return

        if (element.type == Message.TEXT_MESSAGE_STATUS || element.ownerId == User.getMyId()) {
            addMessage(element)
        } else {
            Log.d(Tag.TEST_TAG, "Тип сообщения ${element.type} user id ${element.userId}")
        }
    }

    private fun addMessage(message: Message) {
        cacheMessages.add(message)
        messagesPresenterCallback.setUnchecked()
        messagesPresenterCallback.showMessage(message)
    }

    /**
     * wrote comment then update our message to [Message.TEXT_MESSAGE_STATUS] and show it
     */
    override fun returnUpdatedCallback(obj: Message) {
        if (cacheMessages.find { it.id == obj.id } == null) {
            cacheMessages.add(obj)
            messagesPresenterCallback.showMessage(obj)
        }
    }

    override fun getId(userId: String, dialogId: String): String {
        return messageRepository.getIdForNew(userId, dialogId)
    }
}