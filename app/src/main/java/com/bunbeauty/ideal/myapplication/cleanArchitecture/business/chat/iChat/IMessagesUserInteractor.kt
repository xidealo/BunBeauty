package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat.iChat

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.chat.MessagesPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface IMessagesUserInteractor {
    fun getCompanionUser(messagesPresenterCallback: MessagesPresenterCallback)
    fun getCacheCurrentUser(): User
}