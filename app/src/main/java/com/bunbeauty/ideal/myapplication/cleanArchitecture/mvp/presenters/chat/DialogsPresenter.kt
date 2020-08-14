package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.chat

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat.iChat.IDialogsDialogInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat.iChat.IDialogsMessageInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat.iChat.IDialogsUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.chat.DialogsPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.chat.DialogsView

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

    fun getDialogsLink() = dialogsDialogInteractor.getDialogsLink()

    override fun getUser(dialog: Dialog) {
        dialogsUserInteractor.getUser(dialog, this)
    }

    override fun fillDialogs(user: User) {
        dialogsDialogInteractor.fillDialogs(user, this)
    }

    override fun showDialogs(dialogs: List<Dialog>) {
        viewState.hideLoading()
        viewState.hideEmptyDialogs()
        viewState.showDialogs(dialogs)
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