package com.bunbeauty.ideal.myapplication.clean_architecture.business.chat.i_chat

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.chat.DialogsPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User

interface IDialogsDialogInteractor {
    fun getDialogs(dialogsPresenterCallback: DialogsPresenterCallback)
    fun getDialogsLink(): List<Dialog>
    fun fillDialogs(user: User, dialogsPresenterCallback: DialogsPresenterCallback)
    fun fillDialogsByMessages(
        message: Message,
        dialogsPresenterCallback: DialogsPresenterCallback
    )
}