package com.bunbeauty.ideal.myapplication.clean_architecture.business.chat

import com.bunbeauty.ideal.myapplication.clean_architecture.business.chat.i_chat.IDialogsMessageInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.chat.DialogsPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.message.MessageCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.IMessageRepository

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
            companionId,
            this
        )
    }

    override fun returnGottenObject(element: Message?) {

        if (element == null) return

        val foundMessage = cacheMyMessages.find { it.userId == element.userId }

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