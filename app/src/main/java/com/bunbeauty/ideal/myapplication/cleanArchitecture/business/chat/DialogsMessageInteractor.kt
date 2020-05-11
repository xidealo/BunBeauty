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
    private var dialogsCount = 0
    private var currentDialogsCount = 0
    private var cacheMyMessages = mutableListOf<Message>()

    override fun getMyMessages(
        dialogs: List<Dialog>,
        companionDialogs: List<Dialog>,
        dialogsPresenterCallback: DialogsPresenterCallback
    ) {
        dialogsCount = dialogs.size + companionDialogs.size
        this.dialogsPresenterCallback = dialogsPresenterCallback
        for (dialog in dialogs) {
            messageRepository.getByIdLastMessage(
                dialog, this
            )
        }

        for (dialog in companionDialogs) {
            messageRepository.getByIdLastMessage(
                dialog, this
            )
        }
    }

    override fun returnElement(element: Message) {
        currentDialogsCount++

        val foundMessage = cacheMyMessages.find { it.dialogId == element.dialogId }
        if (foundMessage == null) {
            if(element.id.isNotEmpty())
            cacheMyMessages.add(element)
        }else{
            if(foundMessage.time < element.time){
                cacheMyMessages.remove(foundMessage)
                cacheMyMessages.add(element)
            }
        }

        if (dialogsCount == currentDialogsCount) {
            dialogsPresenterCallback.fillDialogsByMessages(cacheMyMessages)
        }
    }

}