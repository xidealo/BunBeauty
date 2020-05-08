package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat.iChat.IMessagesUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.chat.MessagesPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

class MessagesUserInteractor(private val intent: Intent) : IMessagesUserInteractor {
    private lateinit var cacheCurrentUser: User

    override fun getCacheCurrentUser() = cacheCurrentUser

    override fun getCompanionUser(messagesPresenterCallback: MessagesPresenterCallback) {
        cacheCurrentUser = intent.getSerializableExtra(User.USER) as User
        messagesPresenterCallback.showCompanionUserInfo(
            "${cacheCurrentUser.name} ${cacheCurrentUser.surname}",
            cacheCurrentUser.photoLink
        )
    }
}