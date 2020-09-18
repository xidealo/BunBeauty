package com.bunbeauty.ideal.myapplication.clean_architecture.domain.commets.creation_comment.i_creation_comment

import android.content.Intent
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.comments.CreationCommentPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.ServiceComment
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.UserComment

interface ICreationCommentMessageInteractor {
    fun updateUserCommentMessage(
        intent: Intent,
        userComment: UserComment,
        creationCommentPresenterCallback: CreationCommentPresenterCallback
    )

    fun updateServiceCommentMessage(
        intent: Intent,
        serviceComment: ServiceComment,
        creationCommentPresenterCallback: CreationCommentPresenterCallback
    )

    fun checkMessage(
        intent: Intent,
        rating: Float,
        review: String,
        creationCommentPresenterCallback: CreationCommentPresenterCallback
    )
}