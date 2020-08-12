package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.chat

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface DialogsPresenterCallback {
    fun getUser(dialog: Dialog)
    fun fillDialogs(user: User)
    fun showDialogs(dialogs: List<Dialog>)
    fun getMessage(myId: String, companionId: String)
    fun fillDialogsByMessages(message: Message)
    fun showLoading()
    fun hideLoading()
    fun showEmptyDialogs()
    fun hideEmptyDialogs()
}