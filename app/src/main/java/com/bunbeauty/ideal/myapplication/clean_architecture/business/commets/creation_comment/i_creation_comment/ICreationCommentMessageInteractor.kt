package com.bunbeauty.ideal.myapplication.clean_architecture.business.commets.creation_comment.i_creation_comment

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.comments.CreationCommentPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.ServiceComment
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.UserComment

interface ICreationCommentMessageInteractor {
    fun updateUserCommentMessage(
        userComment: UserComment,
        creationCommentPresenterCallback: CreationCommentPresenterCallback
    )

    fun updateServiceCommentMessage(
        serviceComment: ServiceComment,
        creationCommentPresenterCallback: CreationCommentPresenterCallback
    )

    fun checkMessage(
        rating: Float,
        review: String,
        creationCommentPresenterCallback: CreationCommentPresenterCallback
    )
}