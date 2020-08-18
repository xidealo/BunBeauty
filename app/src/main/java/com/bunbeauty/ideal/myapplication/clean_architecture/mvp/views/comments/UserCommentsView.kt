package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.comments

import com.arellomobile.mvp.MvpView
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.UserComment

interface UserCommentsView : MvpView {
    fun showLoading()
    fun hideLoading()
    fun updateUserComments(userComments: List<UserComment>)
    fun showUserComments()
    fun hideUserComments()
    fun showEmptyScreen()
    fun hideEmptyScreen()
}