package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.userComments.iUserComments

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.comments.UserCommentsPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.UserComment

interface IUserCommentsUserCommentInteractor {
    fun getUserCommentsLink(): List<UserComment>
    fun getUserComments(user: User, userCommentsPresenterCallback: UserCommentsPresenterCallback)
    fun setUserOnUserComment(
        user: User,
        userCommentsPresenterCallback: UserCommentsPresenterCallback
    )
}