package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat.iChat.IDialogsMessageInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.chat.DialogsPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.message.MessagesCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.MessageRepository

class DialogsMessageInteractor(private val messageRepository: MessageRepository) :
    IDialogsMessageInteractor, MessagesCallback {

    private lateinit var dialogsPresenterCallback: DialogsPresenterCallback
    private var dialogsCount = 0
    private var currentDialogsCount = 0
    private var cacheMessages = mutableListOf<Message>()

    override fun getMessages(
        dialogs: List<Dialog>,
        dialogsPresenterCallback: DialogsPresenterCallback
    ) {
        dialogsCount = dialogs.size
        this.dialogsPresenterCallback = dialogsPresenterCallback
        for (dialog in dialogs) {
            messageRepository.getById(
                Message(
                    id = dialog.lastMessage.id,
                    dialogId = dialog.id,
                    userId = dialog.ownerId
                ), this
            )
        }
    }

    override fun returnList(objects: List<Message>) {
        currentDialogsCount++
        cacheMessages.addAll(objects)

        if (dialogsCount == currentDialogsCount) {
            dialogsPresenterCallback.fillDialogsByMessages(cacheMessages)
        }
    }

}