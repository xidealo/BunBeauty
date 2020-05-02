package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.chat

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat.iChat.IMessagesMessageInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.chat.MessagesView

@InjectViewState
class MessagesPresenter(private val messageInteractor: IMessagesMessageInteractor) :
    MvpPresenter<MessagesView>() {

    fun getMessages(){

    }

}