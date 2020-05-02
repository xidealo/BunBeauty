package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.chat

import com.arellomobile.mvp.MvpView
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog

interface DialogsView:MvpView {
    fun showDialogs(dialogList: List<Dialog>)
    fun showLoading()
    fun hideLoading()
    fun showEmptyDialogs()
    fun hideEmptyDialogs()
}