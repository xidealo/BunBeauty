package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.chat

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat.iChat.IMessagesDialogInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat.iChat.IMessagesMessageInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat.iChat.IMessagesUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.chat.MessagesPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.chat.MessagesView
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithTimeApi
import java.util.*

@InjectViewState
class MessagesPresenter(
    private val messagesMessageInteractor: IMessagesMessageInteractor,
    private val messagesDialogInteractor: IMessagesDialogInteractor,
    private val messagesUserInteractor: IMessagesUserInteractor
) :
    MvpPresenter<MessagesView>(), MessagesPresenterCallback {

    fun getMessagesLink() = messagesMessageInteractor.getMyMessagesLink()

    fun getCompanionUser() = messagesUserInteractor.getCompanionUser(this)

    fun createMessageScreen() {
        messagesMessageInteractor.getMyMessages(
            messagesDialogInteractor.getCompanionDialog(),
            this
        )
        messagesMessageInteractor.getMyMessages(
            messagesDialogInteractor.getMyDialog(),
            this
        )
    }

    fun checkMoveToStart() {
        messagesMessageInteractor.checkMoveToStart(
            messagesMessageInteractor.getMyMessagesLink(),
            this
        )
    }

    fun sendMessage(messageText: String) {
        val message = Message()
        message.message = messageText
        message.dialogId = messagesDialogInteractor.getCompanionDialog().id
        message.userId = messagesDialogInteractor.getCompanionDialog().ownerId
        message.time = WorkWithTimeApi.getDateInFormatYMDHMS(Date())
        messagesMessageInteractor.sendMessage(message, this)
    }

    fun goToProfile() {
        viewState.goToProfile(messagesUserInteractor.getCacheCurrentUser())
    }

    override fun showMessagesScreen(messages: List<Message>) {
        viewState.hideLoading()
        viewState.showMessagesScreen(messages)
    }

    override fun showSendMessage(message: Message) {
        viewState.showSendMessage(message)
    }

    override fun showMoveToStart() {
        viewState.moveToStart()
    }

    override fun showCompanionUserInfo(fullName: String, photoLink: String) {
        viewState.showCompanionUser(fullName, photoLink)
    }

}