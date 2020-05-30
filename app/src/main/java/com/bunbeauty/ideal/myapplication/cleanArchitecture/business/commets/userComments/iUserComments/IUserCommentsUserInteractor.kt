package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.userComments.iUserComments

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.comments.UserCommentsPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.UserComment

interface IUserCommentsUserInteractor {
    fun getCurrentUser(): User
    fun getUsers(
        userComment: UserComment,
        userCommentsPresenterCallback: UserCommentsPresenterCallback
    )
}