package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.chat

import com.arellomobile.mvp.MvpView
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Dialog

interface DialogsView:MvpView {
    fun showDialogs(dialog: Dialog)
    fun showLoading()
    fun hideLoading()
    fun showEmptyDialogs()
    fun hideEmptyDialogs()
}