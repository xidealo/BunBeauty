package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.chat

import com.arellomobile.mvp.MvpView
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Message

interface MessagesView:MvpView {
    fun showMessagesScreen(messages:List<Message>)
    fun showSendMessage(message: Message)
    fun hideLoading()
    fun showLoading()
}