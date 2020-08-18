package com.bunbeauty.ideal.myapplication.clean_architecture.callback.comments

import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.UserComment

interface UserCommentsPresenterCallback {
    fun getUser(userComment: UserComment)
    fun setUserOnUserComment(user: User)
    fun updateUserComments(userComments: List<UserComment>)
    fun showEmptyScreen()
}