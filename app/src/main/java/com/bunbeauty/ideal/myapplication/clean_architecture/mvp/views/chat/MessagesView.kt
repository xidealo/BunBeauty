package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.chat

import com.arellomobile.mvp.MvpView
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User

interface MessagesView : MvpView {
    fun showMessage(message: Message, isSmoothScrollingToPosition: Boolean)
    fun updateMessageAdapter(message: Message)
    fun removeMessageAdapter(message: Message)
    fun moveToStart()
    fun hideLoading()
    fun showLoading()
    fun showEmptyScreen()
    fun hideEmptyScreen()
    fun showCompanionUser(fullName: String, photoLink: String)
    fun goToProfile(user: User)
    fun goToCreationComment(user: User, message: Message, dialog: Dialog)
}