package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat.iChat.IMessagesUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.chat.MessagesPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

class MessagesUserInteractor(private val intent: Intent) : IMessagesUserInteractor {
    private lateinit var cacheCompanionUser: User

    override fun getCacheCompanionUser() = cacheCompanionUser

    override fun getCompanionUser(messagesPresenterCallback: MessagesPresenterCallback) {
        cacheCompanionUser = intent.getSerializableExtra(User.USER) as User
        messagesPresenterCallback.showCompanionUserInfo(
            "${cacheCompanionUser.name} ${cacheCompanionUser.surname}",
            cacheCompanionUser.photoLink
        )
    }
}