package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat.iChat.IDialogsMessageInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.chat.DialogsPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.message.MessageCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.MessageRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IMessageRepository

class DialogsMessageInteractor(private val messageRepository: IMessageRepository) :
    IDialogsMessageInteractor, MessageCallback {

    private lateinit var dialogsPresenterCallback: DialogsPresenterCallback
    private var cacheMyMessages = mutableListOf<Message>()

    override fun getLastMessage(
        myId: String,
        companionId: String,
        dialogsPresenterCallback: DialogsPresenterCallback
    ) {
        this.dialogsPresenterCallback = dialogsPresenterCallback
        messageRepository.getByIdLastMessage(
            myId,
            companionId, this
        )
    }

    override fun returnElement(element: Message?) {

        if (element == null) return

        val foundMessage = cacheMyMessages.find { it.dialogId == element.dialogId }

        if (foundMessage == null) {
            cacheMyMessages.add(element)
            dialogsPresenterCallback.fillDialogsByMessages(element)
        } else {
            if (foundMessage.time < element.time) {
                cacheMyMessages.remove(foundMessage)
                cacheMyMessages.add(element)
                dialogsPresenterCallback.fillDialogsByMessages(element)
            } else {
                dialogsPresenterCallback.fillDialogsByMessages(foundMessage)
            }
        }
    }

}