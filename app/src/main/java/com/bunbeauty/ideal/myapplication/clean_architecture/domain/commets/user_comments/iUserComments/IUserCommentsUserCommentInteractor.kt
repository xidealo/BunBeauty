package com.bunbeauty.ideal.myapplication.clean_architecture.domain.commets.user_comments.iUserComments

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.comments.UserCommentsPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.UserComment

interface IUserCommentsUserCommentInteractor {
    fun getUserCommentsLink(): List<UserComment>
    fun getUserComments(
        user: User,
        loadingLimit: Int,
        userCommentsPresenterCallback: UserCommentsPresenterCallback
    )

    fun setUserOnUserComment(
        user: User,
        userCommentsPresenterCallback: UserCommentsPresenterCallback
    )
}