package com.bunbeauty.ideal.myapplication.clean_architecture.domain.commets.creation_comment.i_creation_comment

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.comments.CreationCommentPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.ServiceComment

interface ICreationCommentServiceCommentInteractor {
    fun setRating(rating: Float)
    fun setReview(review: String)
    fun createServiceComment(
        serviceComment: ServiceComment,
        creationCommentPresenterCallback: CreationCommentPresenterCallback
    )
}