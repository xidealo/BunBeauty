package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat.iChat.IDialogsMessageInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.chat.DialogsPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.message.MessageCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.MessageRepository

class DialogsMessageInteractor(private val messageRepository: MessageRepository) :
    IDialogsMessageInteractor, MessageCallback {

    private lateinit var dialogsPresenterCallback: DialogsPresenterCallback
    private var currentDialogsCount = 0
    private var cacheMyMessages = mutableListOf<Message>()

    override fun getLastMessage(
        dialog: Dialog,
        dialogsPresenterCallback: DialogsPresenterCallback
    ) {
        this.dialogsPresenterCallback = dialogsPresenterCallback
        messageRepository.getByIdLastMessage(dialog, this)
    }

    override fun clearCache() {
        currentDialogsCount = 0
        cacheMyMessages.clear()
    }

    override fun returnElement(element: Message) {

        val foundMessage = cacheMyMessages.find { it.dialogId == element.dialogId }

        if (foundMessage == null) {
            if (element.id.isNotEmpty()) {
                cacheMyMessages.add(element)
            }
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