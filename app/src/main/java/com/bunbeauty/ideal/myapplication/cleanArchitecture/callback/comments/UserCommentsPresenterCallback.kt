package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.comments

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.UserComment

interface UserCommentsPresenterCallback {
    fun getUsers(userComments: List<UserComment>)
    fun setUserOnUserComment(user: User)
    fun updateUserComments()
}