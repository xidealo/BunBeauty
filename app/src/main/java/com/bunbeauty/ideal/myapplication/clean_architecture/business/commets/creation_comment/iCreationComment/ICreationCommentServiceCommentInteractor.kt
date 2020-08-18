package com.bunbeauty.ideal.myapplication.clean_architecture.business.commets.creation_comment.iCreationComment

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