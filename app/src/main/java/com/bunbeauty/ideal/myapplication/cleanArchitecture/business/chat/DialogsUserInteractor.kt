package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat.iChat.IDialogsUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.chat.DialogsPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.user.UsersCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.UserRepository

class DialogsUserInteractor(private val userRepository: UserRepository) :
    IDialogsUserInteractor, UsersCallback {

    private lateinit var dialogsPresenterCallback: DialogsPresenterCallback

    override fun getUser(
        dialog: Dialog,
        dialogsPresenterCallback: DialogsPresenterCallback
    ) {
        this.dialogsPresenterCallback = dialogsPresenterCallback
        userRepository.getById(dialog.user.id, this, true)
    }

    override fun returnUsers(users: List<User>) {
        dialogsPresenterCallback.fillDialogs(users.first())
    }

}