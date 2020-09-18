package com.bunbeauty.ideal.myapplication.clean_architecture.domain.chat.i_chat.message

import android.content.Intent
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.chat.i_chat.message.i_message.IMessagesUserInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.chat.MessagesPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User

class MessagesUserInteractor : IMessagesUserInteractor {
    private lateinit var cacheCompanionUser: User

    override fun getCacheCompanionUser() = cacheCompanionUser

    override fun updateUser(user: User) {
        cacheCompanionUser = user
    }

    override fun getCompanionUser(intent: Intent, messagesPresenterCallback: MessagesPresenterCallback) {
        cacheCompanionUser = intent.getSerializableExtra(User.USER) as User
        messagesPresenterCallback.showCompanionUserInfo(
            "${cacheCompanionUser.name} ${cacheCompanionUser.surname}",
            cacheCompanionUser.photoLink
        )
    }
}