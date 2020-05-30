package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.comments

import com.arellomobile.mvp.MvpView

interface UserCommentsView : MvpView {
    fun showLoading()
    fun hideLoading()
    fun updateUserComments()
    fun showUserComments()
    fun hideUserComments()
    fun showEmptyScreen()
    fun hideEmptyScreen()
}