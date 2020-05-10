package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat.iChat.IDialogsDialogInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.chat.DialogsPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.dialog.DialogsCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.DialogRepository

class DialogsDialogInteractor(private val dialogRepository: DialogRepository) :
    IDialogsDialogInteractor, DialogsCallback {

    private var dialogs = mutableListOf<Dialog>()
    private lateinit var dialogsPresenterCallback: DialogsPresenterCallback

    override fun getDialogsLink() = dialogs

    override fun getDialogs(dialogsPresenterCallback: DialogsPresenterCallback) {
        this.dialogsPresenterCallback = dialogsPresenterCallback
        dialogRepository.getByUserId(User.getMyId(), this)
    }

    override fun returnList(objects: List<Dialog>) {

        if (objects.isEmpty()) {
            dialogsPresenterCallback.showEmptyDialogs()
            dialogsPresenterCallback.hideLoading()
            return
        }

        dialogs.addAll(objects)
        dialogsPresenterCallback.getUsers(dialogs)
    }

    override fun fillDialogs(
        users: List<User>,
        dialogsPresenterCallback: DialogsPresenterCallback
    ) {
        for (user in users) {
            val dialogWithUserId = dialogs.find { it.user.id == user.id }
            dialogWithUserId!!.user = user
        }
        dialogsPresenterCallback.getMessages(dialogs)
    }

    override fun fillDialogsByMessages(
        messages: List<Message>,
        dialogsPresenterCallback: DialogsPresenterCallback
    ) {
        for (message in messages) {
            val dialogWithMessageId = dialogs.find { it.lastMessage.id == message.id }
            if (dialogWithMessageId != null) {
                dialogWithMessageId.lastMessage = message
            }
        }
        dialogsPresenterCallback.showDialogs(dialogs)
    }
}