package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.chat

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat.iChat.IMessagesDialogInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat.iChat.IMessagesMessageInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat.iChat.IMessagesUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.chat.MessagesPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.chat.MessagesView

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
        message.userId = messagesDialogInteractor.getMyDialog().ownerId
        //message.isText = true
        //message.isUserReview = true
        message.isServiceReview= true
        messagesMessageInteractor.sendMessage(message, this)
    }

    fun getCacheCurrentUser() = messagesUserInteractor.getCacheCurrentUser()

    fun goToProfile() {
        viewState.goToProfile(getCacheCurrentUser())
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

    override fun setUnchecked() {
        messagesDialogInteractor.setUnchecked()
    }

    override fun updateUncheckedDialog(message: Message) {
        messagesDialogInteractor.updateUncheckedDialog(message)
    }

    fun updateCheckedDialog() {
        messagesDialogInteractor.updateCheckedDialog()
    }

    override fun showCompanionUserInfo(fullName: String, photoLink: String) {
        viewState.showCompanionUser(fullName, photoLink)
    }

}