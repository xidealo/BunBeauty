package com.bunbeauty.ideal.myapplication.clean_architecture.domain.commets.user_comments.iUserComments

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.comments.UserCommentsPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.UserComment

interface IUserCommentsUserInteractor {
    fun getCurrentUser(): User
    fun getUsers(
        userComment: UserComment,
        userCommentsPresenterCallback: UserCommentsPresenterCallback
    )
}