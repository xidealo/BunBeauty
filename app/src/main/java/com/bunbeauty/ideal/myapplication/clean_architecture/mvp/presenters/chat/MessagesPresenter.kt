package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.chat

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.chat.i_chat.message.i_message.*
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.chat.MessagesPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Order
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.chat.MessagesView

@InjectViewState
class MessagesPresenter(
    private val messagesMessageInteractor: IMessagesMessageInteractor,
    private val messagesDialogInteractor: IMessagesDialogInteractor,
    private val messagesUserInteractor: IMessagesUserInteractor,
    private val messagesOrderInteractor: IMessagesOrderInteractor,
    private val messageScheduleInteractor: IMessageScheduleInteractor
) : MvpPresenter<MessagesView>(), MessagesPresenterCallback {

    fun getCompanionUser() = messagesUserInteractor.getCompanionUser(this)

    fun removeObservers() {
        messagesMessageInteractor.removeObservers()
    }

    fun setIsSmoothScrollingToPosition(isSmoothScrollingToPosition: Boolean) {
        messagesMessageInteractor.setIsSmoothScrollingToPosition(isSmoothScrollingToPosition)
    }

    fun createMessageScreen(loadingLimit: Int) {
        messagesMessageInteractor.getMessages(
            messagesDialogInteractor.getMyDialog(),
            loadingLimit,
            this
        )
    }

    fun updateMessage(message: Message) {
        messagesMessageInteractor.updateMessages(message, this)
    }

    fun updateUser(user: User) {
        messagesUserInteractor.updateUser(user)
    }

    override fun sendMessage(messageText: String) {
        setIsSmoothScrollingToPosition(true)

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
        myMessage.type = Message.TEXT_STATUS
        messagesMessageInteractor.sendMessage(myMessage, this)

        //добавление в диалог копманиона
        val companionMessage = Message()
        companionMessage.id = messageId
        companionMessage.ownerId = User.getMyId()
        companionMessage.message = messageText
        companionMessage.dialogId = messagesDialogInteractor.getCompanionDialog().id
        companionMessage.userId = messagesDialogInteractor.getCompanionDialog().user.id
        companionMessage.type = Message.TEXT_STATUS
        messagesMessageInteractor.sendMessage(companionMessage, this)
    }

    fun goToProfile() {
        viewState.goToProfile(messagesUserInteractor.getCacheCompanionUser())
    }

    override fun addItemToBottom(message: Message) {
        viewState.addItemToBottom(message)
    }

    override fun addItemToStart(message: Message) {
        viewState.addItemToStart(message)
    }

    override fun moveToStart() {
        viewState.moveToStart()
        viewState.hideLoading()
        viewState.hideEmptyScreen()
    }

    override fun deleteOrderFromSchedule(order: Order) {
        messageScheduleInteractor.deleteOrderFromSchedule(order, this)
    }

    override fun deleteOrder(message: Message) {
        messagesOrderInteractor.deleteOrder(message, this)
    }

    override fun updateMessageAdapter(message: Message) {
        viewState.updateMessageAdapter(message)
    }

    override fun removeMessageAdapter(message: Message) {
        viewState.removeMessageAdapter(message)
    }

    override fun showEmptyScreen() {
        viewState.showEmptyScreen()
        viewState.hideLoading()
    }

    override fun updateUncheckedDialog(message: Message) {
        messagesDialogInteractor.updateUncheckedDialog(message)
    }

    override fun updateCheckedDialog() {
        messagesDialogInteractor.updateCheckedDialog()
    }

    override fun showCompanionUserInfo(fullName: String, photoLink: String) {
        viewState.showCompanionUser(fullName, photoLink)
    }

    fun goToCreationComment(message: Message) {
        viewState.goToCreationComment(
            messagesUserInteractor.getCacheCompanionUser(),
            message,
            messagesDialogInteractor.getMyDialog()
        )
    }

    /**
     * Delete messages
     * Delete order
     * Clear schedule
     * */
    fun cancelOrder(message: Message) {
        setIsSmoothScrollingToPosition(false)
        //my order messages
        messagesMessageInteractor.cancelOrder(message, messagesDialogInteractor.getMyDialog(), this)
        //companion order messages
        messagesMessageInteractor.cancelOrder(
            message,
            messagesDialogInteractor.getCompanionDialog(),
            this
        )
    }
}