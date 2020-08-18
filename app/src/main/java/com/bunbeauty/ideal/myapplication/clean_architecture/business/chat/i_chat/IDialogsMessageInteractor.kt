package com.bunbeauty.ideal.myapplication.clean_architecture.business.chat.i_chat

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.chat.DialogsPresenterCallback

interface IDialogsMessageInteractor {
    fun getLastMessage(
        myId: String,
        companionId: String,
        dialogsPresenterCallback: DialogsPresenterCallback
    )
}