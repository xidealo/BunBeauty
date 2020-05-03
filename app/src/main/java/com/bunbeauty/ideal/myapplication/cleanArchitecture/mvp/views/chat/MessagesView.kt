package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.chat

import com.arellomobile.mvp.MvpView
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface MessagesView : MvpView {
    fun showMessagesScreen(messages: List<Message>)
    fun moveToStart()
    fun showSendMessage(message: Message)
    fun hideLoading()
    fun showLoading()
    fun showCompanionUser(user: User)
    fun goToProfile(user: User)
}