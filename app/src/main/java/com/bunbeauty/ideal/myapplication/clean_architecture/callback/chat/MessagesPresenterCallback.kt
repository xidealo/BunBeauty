package com.bunbeauty.ideal.myapplication.clean_architecture.callback.chat

import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message

interface MessagesPresenterCallback {
    fun showMessagesScreen(messages: List<Message>)
    fun showMoveToStart()
    fun setUnchecked()
    fun updateUncheckedDialog(message: Message)
    fun showCompanionUserInfo(fullName: String, photoLink: String)
}