package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.comments

import com.arellomobile.mvp.MvpView
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.UserComment

interface UserCommentsView : MvpView {
    fun showLoading()
    fun hideLoading()
    fun updateUserComments(userComments: List<UserComment>)
    fun showUserComments()
    fun hideUserComments()
    fun showEmptyScreen()
    fun hideEmptyScreen()
}