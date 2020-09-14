package com.bunbeauty.ideal.myapplication.clean_architecture.domain.chat.i_chat.dialog

import com.bunbeauty.ideal.myapplication.clean_architecture.domain.chat.i_chat.dialog.i_dialog.IDialogsUserInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.chat.DialogsPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.user.UserCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.UserRepository

class DialogsUserInteractor(private val userRepository: UserRepository) :
    IDialogsUserInteractor, UserCallback {

    private lateinit var dialogsPresenterCallback: DialogsPresenterCallback

    override fun getUser(
        dialog: Dialog,
        dialogsPresenterCallback: DialogsPresenterCallback
    ) {
        this.dialogsPresenterCallback = dialogsPresenterCallback
        userRepository.getById(dialog.user.id, this, true)
    }

    override fun returnGottenObject(element: User?) {
        if (element == null) return

        dialogsPresenterCallback.fillDialogs(element)
    }
}