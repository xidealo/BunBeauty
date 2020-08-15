package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.comments

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.UserComment

interface UserCommentsPresenterCallback {
    fun getUser(userComment: UserComment)
    fun setUserOnUserComment(user: User)
    fun updateUserComments(userComments: List<UserComment>)
    fun showEmptyScreen()
}