package com.bunbeauty.ideal.myapplication.clean_architecture.business.chat.i_chat

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.chat.DialogsPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Dialog

interface IDialogsUserInteractor {
    fun getUser(dialog: Dialog, dialogsPresenterCallback: DialogsPresenterCallback)
}