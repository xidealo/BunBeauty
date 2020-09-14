package com.bunbeauty.ideal.myapplication.clean_architecture.domain.chat.i_chat.dialog.i_dialog

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.chat.DialogsPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Dialog

interface IDialogsUserInteractor {
    fun getUser(dialog: Dialog, dialogsPresenterCallback: DialogsPresenterCallback)
}