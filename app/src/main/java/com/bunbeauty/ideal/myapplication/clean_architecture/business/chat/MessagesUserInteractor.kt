package com.bunbeauty.ideal.myapplication.clean_architecture.business.chat

import android.content.Intent
import com.bunbeauty.ideal.myapplication.clean_architecture.business.chat.i_chat.IMessagesUserInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.chat.MessagesPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User

class MessagesUserInteractor(private val intent: Intent) : IMessagesUserInteractor {
    private lateinit var cacheCompanionUser: User

    override fun getCacheCompanionUser() = cacheCompanionUser

    override fun updateUser(user: User) {
        cacheCompanionUser = user
    }

    override fun getCompanionUser(messagesPresenterCallback: MessagesPresenterCallback) {
        cacheCompanionUser = intent.getSerializableExtra(User.USER) as User
        messagesPresenterCallback.showCompanionUserInfo(
            "${cacheCompanionUser.name} ${cacheCompanionUser.surname}",
            cacheCompanionUser.photoLink
        )
    }
}