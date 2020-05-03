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
    //tree set and sort by time
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
        messageRepository.getByDialogId(dialog, this)
    }

    override fun getCompanionMessages(
        dialog: Dialog,
        messagesPresenterCallback: MessagesPresenterCallback
    ) {
        this.messagesPresenterCallback = messagesPresenterCallback
        messageRepository.getByDialogId(dialog, this)
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
        if(messages.isEmpty()){
            messagesPresenterCallback.showMessagesScreen(cacheMessages)
        }else{
            messagesPresenterCallback.showMessagesScreen(cacheMessages)
            messagesPresenterCallback.showMoveToStart()
        }
    }

    override fun returnList(objects: List<Message>) {
        countOfDialogs++
        cacheMessagesSet.addAll(objects)

        if (countOfDialogs >= 2) {
            cacheMessages.clear()
            cacheMessages.addAll(cacheMessagesSet.sortedBy { it.time })
            checkMoveToStart(cacheMessages, messagesPresenterCallback)
        }
    }

    override fun returnCreatedCallback(obj: Message) {
        messagesPresenterCallback.showSendMessage(obj)
    }
}