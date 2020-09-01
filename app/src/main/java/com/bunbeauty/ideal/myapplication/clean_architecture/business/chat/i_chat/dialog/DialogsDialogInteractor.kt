package com.bunbeauty.ideal.myapplication.clean_architecture.business.chat.i_chat.dialog

import com.bunbeauty.ideal.myapplication.clean_architecture.business.chat.i_chat.dialog.i_dialog.IDialogsDialogInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.chat.DialogsPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.dialog.DialogCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.dialog.DialogChangedCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.dialog.DialogsCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.IDialogRepository

class DialogsDialogInteractor(private val dialogRepository: IDialogRepository) :
    IDialogsDialogInteractor, DialogCallback, DialogsCallback, DialogChangedCallback {

    private var finalCacheDialogs = mutableListOf<Dialog>()
    private var myCacheDialogs = mutableListOf<Dialog>()
    private lateinit var dialogsPresenterCallback: DialogsPresenterCallback

    override fun getDialogsLink() = finalCacheDialogs

    override fun getDialogs(dialogsPresenterCallback: DialogsPresenterCallback) {
        this.dialogsPresenterCallback = dialogsPresenterCallback
        dialogRepository.getByUserId(User.getMyId(), this, this, this)
    }

    override fun returnList(objects: List<Dialog>) {
        if (objects.isEmpty()) {
            dialogsPresenterCallback.showEmptyDialogs()
            dialogsPresenterCallback.hideLoading()
            return
        }

        myCacheDialogs.addAll(objects)

        for (dialog in myCacheDialogs) {
            dialogsPresenterCallback.getUser(dialog)
        }
    }

    override fun returnGottenObject(element: Dialog?) {
        if (element == null) return

        if (element.ownerId == User.getMyId()) {
            myCacheDialogs.add(element)
            dialogsPresenterCallback.getUser(element)
        }
    }

    override fun fillDialogs(
        user: User,
        dialogsPresenterCallback: DialogsPresenterCallback
    ) {
        val dialogWithUserId = myCacheDialogs.find { it.user.id == user.id }
        if (dialogWithUserId != null) {
            dialogWithUserId.user = user
            //получаю последнее сообщение из диалога
            dialogsPresenterCallback.getLastMessage(User.getMyId(), user.id)
        }
    }

    override fun fillDialogsByMessages(
        message: Message,
        dialogsPresenterCallback: DialogsPresenterCallback
    ) {
        val dialogWithMessageId =
            myCacheDialogs.find { it.user.id == message.userId }

        if (dialogWithMessageId != null) {
            dialogWithMessageId.lastMessage = message
            dialogsPresenterCallback.showDialogs(dialogWithMessageId)
        }
    }

    override fun removeReference() {
        dialogRepository.removeObservers()
    }

    override fun returnChanged(element: Dialog) {
        val dialog = myCacheDialogs.find { it.user.id == element.user.id }
        if (dialog != null) {
            dialog.isChecked = element.isChecked
            dialogsPresenterCallback.getLastMessage(element.id, element.user.id)
        }
    }
}