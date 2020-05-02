package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat.iChat.IMessagesMessageInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.chat.MessagesPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.message.InsertMessageCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.message.MessagesCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.MessageRepository

class MessagesMessageInteractor(private val messageRepository: MessageRepository) :
    IMessagesMessageInteractor, MessagesCallback, InsertMessageCallback {

    private var cacheMessages = mutableListOf<Message>()
    private var countOfDialogs = 0
    private lateinit var messagesPresenterCallback: MessagesPresenterCallback

    override fun getMyMessagesLink() = cacheMessages

    override fun getMyMessages(
        dialog: Dialog,
        messagesPresenterCallback: MessagesPresenterCallback
    ) {
        this.messagesPresenterCallback = messagesPresenterCallback
        messageRepository.getByDialogId(dialog, this)
    }


    override fun getCompanionMessages(
        dialog: Dialog,
        messagesPresenterCallback: MessagesPresenterCallback
    ) {
        messageRepository.getByDialogId(dialog, this)
    }

    override fun sendMessage(
        message: Message,
        messagesPresenterCallback: MessagesPresenterCallback
    ) {
        this.messagesPresenterCallback = messagesPresenterCallback
        messageRepository.insert(message, this)
    }

    override fun returnList(objects: List<Message>) {
        countOfDialogs++
        cacheMessages.addAll(objects)
        if (countOfDialogs == 1) {
            messagesPresenterCallback.showMessagesScreen(cacheMessages.sortedBy { it.time })
        }
    }

    override fun returnCreatedCallback(obj: Message) {
        messagesPresenterCallback.showSendMessage(obj)
    }
}