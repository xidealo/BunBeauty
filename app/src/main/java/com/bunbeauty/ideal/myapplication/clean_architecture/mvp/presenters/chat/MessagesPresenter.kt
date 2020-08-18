package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.chat

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.business.chat.i_chat.IMessagesDialogInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.business.chat.i_chat.IMessagesMessageInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.business.chat.i_chat.IMessagesUserInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.chat.MessagesPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.chat.MessagesView

@InjectViewState
class MessagesPresenter(
    private val messagesMessageInteractor: IMessagesMessageInteractor,
    private val messagesDialogInteractor: IMessagesDialogInteractor,
    private val messagesUserInteractor: IMessagesUserInteractor
) : MvpPresenter<MessagesView>(), MessagesPresenterCallback {

    fun getCompanionUser() = messagesUserInteractor.getCompanionUser(this)

    fun createMessageScreen() {
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

    fun updateMessage(message: Message) {
        messagesMessageInteractor.updateMessages(message, this)
    }

    fun updateUser(user: User) {
        messagesUserInteractor.updateUser(user)
    }

    fun sendMessage(messageText: String) {
        val messageId = messagesMessageInteractor.getId(
            messagesDialogInteractor.getMyDialog().ownerId,
            messagesDialogInteractor.getMyDialog().id
        )
        //добавление в мой диалог
        val myMessage = Message()
        myMessage.id = messageId
        myMessage.ownerId = User.getMyId()
        myMessage.message = messageText
        myMessage.dialogId = messagesDialogInteractor.getMyDialog().id
        myMessage.userId = messagesDialogInteractor.getMyDialog().user.id
        myMessage.type = Message.TEXT_MESSAGE_STATUS
        messagesMessageInteractor.sendMessage(myMessage, this)

        //добавление в диалог копманиона
        val companionMessage = Message()
        companionMessage.id = messageId
        companionMessage.ownerId = User.getMyId()
        companionMessage.message = messageText
        companionMessage.dialogId = messagesDialogInteractor.getCompanionDialog().id
        companionMessage.userId = messagesDialogInteractor.getCompanionDialog().user.id
        companionMessage.type = Message.TEXT_MESSAGE_STATUS
        messagesMessageInteractor.sendMessage(companionMessage, this)
    }

    fun getCacheCurrentUser() = messagesUserInteractor.getCacheCompanionUser()

    fun goToProfile() {
        viewState.goToProfile(getCacheCurrentUser())
    }

    override fun showMessagesScreen(messages: List<Message>) {
        viewState.hideLoading()
        viewState.showMessagesScreen(messages)
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

    fun goToCreationComment(message: Message) {
        viewState.goToCreationComment(
            getCacheCurrentUser(),
            message,
            messagesDialogInteractor.getMyDialog()
        )
    }
}