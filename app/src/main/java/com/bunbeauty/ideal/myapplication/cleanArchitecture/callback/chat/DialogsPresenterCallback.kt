package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.chat

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface DialogsPresenterCallback {
    fun getUsers(dialogs: List<Dialog>)
    fun fillDialogs(users:List<User>)
    fun showDialogs(dialogs: List<Dialog>)

    fun showLoading()
    fun hideLoading()
    fun showEmptyDialogs()
    fun hideEmptyDialogs()
}