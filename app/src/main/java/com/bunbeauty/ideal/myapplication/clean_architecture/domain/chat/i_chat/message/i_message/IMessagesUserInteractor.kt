package com.bunbeauty.ideal.myapplication.clean_architecture.domain.chat.i_chat.message.i_message

import android.content.Intent
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.chat.MessagesPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User

interface IMessagesUserInteractor {
    fun getCompanionUser(intent: Intent, messagesPresenterCallback: MessagesPresenterCallback)
    fun updateUser(user: User)
    fun getCacheCompanionUser(): User
}