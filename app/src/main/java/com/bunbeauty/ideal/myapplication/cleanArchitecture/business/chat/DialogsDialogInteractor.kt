package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat.iChat.IDialogsDialogInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.chat.DialogsPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.dialog.DialogCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.dialog.DialogsCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.DialogRepository

class DialogsDialogInteractor(private val dialogRepository: DialogRepository) :
    IDialogsDialogInteractor, DialogsCallback, DialogCallback {

    private var myCacheDialogs = mutableListOf<Dialog>()
    private var companionsCacheDialogs = mutableListOf<Dialog>()
    private lateinit var dialogsPresenterCallback: DialogsPresenterCallback
    override fun getDialogsLink() = myCacheDialogs

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
        myCacheDialogs.addAll(objects)
        dialogsPresenterCallback.getUsers(myCacheDialogs)
    }

    override fun fillDialogs(
        users: List<User>,
        dialogsPresenterCallback: DialogsPresenterCallback
    ) {
        val companionDialogs = mutableListOf<Dialog>()
        for (user in users) {
            val dialogWithUserId = myCacheDialogs.find { it.user.id == user.id }
            if (dialogWithUserId != null) {
                dialogWithUserId.user = user

                companionDialogs.add(
                    Dialog(
                        id = dialogWithUserId.id,
                        ownerId = dialogWithUserId.user.id
                    )
                )

            }

        }
        getCompanionDialogs(companionDialogs)
    }

    private fun getCompanionDialogs(dialogs: List<Dialog>) {
        for (dialog in dialogs) {
            dialogRepository.getById(dialog, this)
        }
    }

    override fun returnElement(element: Dialog) {
        companionsCacheDialogs.add(element)
        if(companionsCacheDialogs.size == myCacheDialogs.size){
            dialogsPresenterCallback.getMessages(myCacheDialogs, companionsCacheDialogs)
        }
    }

    override fun fillDialogsByMessages(
        messages: List<Message>,
        dialogsPresenterCallback: DialogsPresenterCallback
    ) {
        for (message in messages) {
            val dialogWithMessageId = myCacheDialogs.find { it.id== message.dialogId }
            if (dialogWithMessageId != null) {
                dialogWithMessageId.lastMessage = message
            }
        }
        dialogsPresenterCallback.showDialogs(myCacheDialogs)
    }


}