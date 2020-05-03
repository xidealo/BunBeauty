package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.chat

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface MessagesPresenterCallback {
    fun showMessagesScreen(messages:List<Message>)
    fun showSendMessage(message: Message)
    fun showMoveToStart()
    fun showCompanionUserInfo(user: User)
}