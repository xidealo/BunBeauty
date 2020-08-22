package com.bunbeauty.ideal.myapplication.clean_architecture.callback.chat

import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message

interface MessagesPresenterCallback {
    fun showMessage(message: Message)
    fun updateMessageAdapter(message: Message)
    fun showMoveToStart()
    fun setUnchecked()
    fun showEmptyScreen()
    fun updateUncheckedDialog(message: Message)
    fun showCompanionUserInfo(fullName: String, photoLink: String)
}