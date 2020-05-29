package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat.iChat.IMessagesMessageInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.chat.MessagesPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.message.InsertMessageCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.message.MessageCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.message.MessagesCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.message.UpdateMessageCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.MessageRepository

class MessagesMessageInteractor(private val messageRepository: MessageRepository) :
    IMessagesMessageInteractor, MessagesCallback, InsertMessageCallback, MessageCallback,
    UpdateMessageCallback {

    private var cacheMessages = mutableListOf<Message>()
    private var cacheMessagesSet = mutableSetOf<Message>()
    private var countOfDialogs = 0
    private lateinit var messagesPresenterCallback: MessagesPresenterCallback

    override fun getMyMessagesLink() = cacheMessages

    override fun getMyMessages(
        dialog: Dialog,
        messagesPresenterCallback: MessagesPresenterCallback
    ) {
        this.messagesPresenterCallback = messagesPresenterCallback
        messageRepository.getByDialogId(dialog, this, this, this)
    }

    override fun getCompanionMessages(
        dialog: Dialog,
        messagesPresenterCallback: MessagesPresenterCallback
    ) {
        this.messagesPresenterCallback = messagesPresenterCallback
        messageRepository.getByDialogId(dialog, this, this, this)
    }

    override fun updateMessages(
        message: Message,
        messagesPresenterCallback: MessagesPresenterCallback
    ) {
        cacheMessages.remove(cacheMessages.find { it.id == message.id }!!)
        cacheMessages.add(message)
        messagesPresenterCallback.showSendMessage(message)
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

    override fun checkMoveToStart(
        messages: List<Message>,
        messagesPresenterCallback: MessagesPresenterCallback
    ) {
        if (messages.isEmpty()) {
            messagesPresenterCallback.showMessagesScreen(cacheMessages)
        } else {
            messagesPresenterCallback.showMessagesScreen(cacheMessages)
            messagesPresenterCallback.showMoveToStart()
        }
    }

    override fun returnList(objects: List<Message>) {
        countOfDialogs++

        for (message in objects) {
            if (message.type == Message.TEXT_MESSAGE_STATUS) {
                cacheMessagesSet.add(message)
            } else {
                if (message.userId == User.getMyId()) {
                    cacheMessages.add(message)
                }
            }
        }

        //cacheMessagesSet.addAll(objects)
        if (countOfDialogs == 2) {
            cacheMessages.addAll(cacheMessagesSet.sortedBy { it.time })
            checkMoveToStart(cacheMessages, messagesPresenterCallback)
        }
    }

    override fun returnElement(element: Message) {
        if (element.type == Message.TEXT_MESSAGE_STATUS) {
            addMessage(element)
        } else {
            if (element.userId == User.getMyId()) {
                addMessage(element)
            }
        }
    }

    private fun addMessage(message: Message) {
        cacheMessages.add(message)
        messagesPresenterCallback.setUnchecked()
        checkMoveToStart(cacheMessages, messagesPresenterCallback)
    }

    override fun returnCreatedCallback(obj: Message) {
        messagesPresenterCallback.updateUncheckedDialog(obj)
        messagesPresenterCallback.showSendMessage(obj)
    }

    override fun returnUpdatedCallback(obj: Message) {
        val k = 0
    }

}