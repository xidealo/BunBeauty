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

    private var finalCacheDialogs = mutableListOf<Dialog>()
    private var myCacheDialogs = mutableListOf<Dialog>()
    private var companionsCacheDialogs = mutableListOf<Dialog>()
    private lateinit var dialogsPresenterCallback: DialogsPresenterCallback
    private var dialogCount = 0
    override fun getDialogsLink() = finalCacheDialogs

    override fun getDialogs(dialogsPresenterCallback: DialogsPresenterCallback) {
        this.dialogsPresenterCallback = dialogsPresenterCallback
        dialogRepository.getByUserId(User.getMyId(), this, this)
    }

    override fun returnList(objects: List<Dialog>) {
        if (objects.isEmpty()) {
            dialogsPresenterCallback.showEmptyDialogs()
            dialogsPresenterCallback.hideLoading()
            return
        }

        myCacheDialogs.addAll(objects)

        for (dialog in objects) {
            dialogsPresenterCallback.getUser(dialog)
            dialogsPresenterCallback.getMessage(dialog)
        }
    }

    override fun fillDialogs(
        user: User,
        dialogsPresenterCallback: DialogsPresenterCallback
    ) {
        var companionDialog = Dialog()

        val dialogWithUserId = myCacheDialogs.find { it.user.id == user.id }
        if (dialogWithUserId != null) {
            dialogWithUserId.user = user
            companionDialog = Dialog(
                id = dialogWithUserId.id,
                ownerId = dialogWithUserId.user.id
            )
        }
        getCompanionDialog(companionDialog)
        dialogsPresenterCallback.showDialogs(finalCacheDialogs)
    }

    private fun getCompanionDialog(dialog: Dialog) {
        dialogRepository.getById(dialog, this)
    }

    override fun returnElement(element: Dialog) {
        if (element.ownerId == User.getMyId()) {
            myCacheDialogs.add(element)
            dialogsPresenterCallback.getUser(element)
        } else {
            companionsCacheDialogs.add(element)
        }
        dialogsPresenterCallback.getMessage(element)
    }

    override fun fillDialogsByMessages(
        message: Message,
        dialogsPresenterCallback: DialogsPresenterCallback
    ) {
        dialogCount++

        val dialogWithMessageId = myCacheDialogs.find { it.id == message.dialogId }

        if (dialogWithMessageId != null) {
            dialogWithMessageId.lastMessage = message
        }

        if (dialogCount == myCacheDialogs.size) {
            finalCacheDialogs.clear()
            finalCacheDialogs.addAll(myCacheDialogs.sortedByDescending { it.lastMessage.time })
            dialogsPresenterCallback.showDialogs(finalCacheDialogs)
        }

    }
}