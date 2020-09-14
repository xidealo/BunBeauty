package com.bunbeauty.ideal.myapplication.clean_architecture.domain.chat.i_chat.dialog.i_dialog

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.chat.DialogsPresenterCallback

interface IDialogsMessageInteractor {
    fun getLastMessage(
        myId: String,
        companionId: String,
        dialogsPresenterCallback: DialogsPresenterCallback
    )

    fun removeObservers()
}