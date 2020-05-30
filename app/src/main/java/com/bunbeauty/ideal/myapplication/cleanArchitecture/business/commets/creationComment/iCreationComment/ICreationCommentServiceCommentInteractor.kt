package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.creationComment.iCreationComment

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.comments.CreationCommentPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.ServiceComment

interface ICreationCommentServiceCommentInteractor {
    fun setRating(rating: Float)
    fun setReview(review: String)
    fun createServiceComment(
        serviceComment: ServiceComment,
        creationCommentPresenterCallback: CreationCommentPresenterCallback
    )
}