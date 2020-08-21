package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.chat

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.business.chat.i_chat.IDialogsDialogInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.business.chat.i_chat.IDialogsMessageInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.business.chat.i_chat.IDialogsUserInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.chat.DialogsPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.chat.DialogsView

@InjectViewState
class DialogsPresenter(
    private val dialogsDialogInteractor: IDialogsDialogInteractor,
    private val dialogsUserInteractor: IDialogsUserInteractor,
    private val dialogsMessageInteractor: IDialogsMessageInteractor
) : MvpPresenter<DialogsView>(), DialogsPresenterCallback {

    fun getDialogs() {
        viewState.showLoading()
        dialogsDialogInteractor.getDialogs(this)
    }

    override fun getUser(dialog: Dialog) {
        dialogsUserInteractor.getUser(dialog, this)
    }

    override fun fillDialogs(user: User) {
        dialogsDialogInteractor.fillDialogs(user, this)
    }

    override fun showDialogs(dialog: Dialog) {
        viewState.hideLoading()
        viewState.hideEmptyDialogs()
        viewState.showDialogs(dialog)
    }

    override fun getLastMessage(myId: String, companionId: String) {
        dialogsMessageInteractor.getLastMessage(myId, companionId, this)
    }

    override fun fillDialogsByMessages(message: Message) {
        dialogsDialogInteractor.fillDialogsByMessages(message, this)
    }

    override fun showLoading() {
        viewState.showLoading()
    }

    override fun hideLoading() {
        viewState.hideLoading()
    }

    override fun showEmptyDialogs() {
        viewState.showEmptyDialogs()
    }

    override fun hideEmptyDialogs() {
        viewState.hideEmptyDialogs()
    }

}