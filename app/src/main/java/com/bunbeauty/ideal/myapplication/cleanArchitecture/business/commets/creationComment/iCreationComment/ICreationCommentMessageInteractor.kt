package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.creationComment.iCreationComment

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.comments.CreationCommentPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.ServiceComment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.UserComment

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