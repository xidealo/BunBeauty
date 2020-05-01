package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat.iChat.IDialogsUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.chat.DialogsPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.user.UserCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.UserRepository

class DialogsUserInteractor(private val userRepository: UserRepository) :
    IDialogsUserInteractor, UserCallback {

    private lateinit var dialogsPresenterCallback: DialogsPresenterCallback
    private var users = mutableListOf<User>()
    private var dialogsCount = 0
    private var currentDialogsCount = 0

    override fun getUsers(
        dialogs: List<Dialog>,
        dialogsPresenterCallback: DialogsPresenterCallback
    ) {
        this.dialogsPresenterCallback = dialogsPresenterCallback
        dialogsCount = dialogs.size

        for (dialog in dialogs) {
            userRepository.getById(dialog.user.id, this, true)
        }
    }

    override fun returnUser(user: User) {
        this.users.add(user)
        currentDialogsCount++

        if (dialogsCount == currentDialogsCount) {
            dialogsPresenterCallback.fillDialogs(users)
        }
    }

}