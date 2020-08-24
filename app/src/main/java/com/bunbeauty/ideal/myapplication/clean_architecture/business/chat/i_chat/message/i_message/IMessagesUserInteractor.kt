package com.bunbeauty.ideal.myapplication.clean_architecture.business.chat.i_chat.message.i_message

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.chat.MessagesPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User

interface IMessagesUserInteractor {
    fun getCompanionUser(messagesPresenterCallback: MessagesPresenterCallback)
    fun updateUser(user: User)
    fun getCacheCompanionUser(): User
}