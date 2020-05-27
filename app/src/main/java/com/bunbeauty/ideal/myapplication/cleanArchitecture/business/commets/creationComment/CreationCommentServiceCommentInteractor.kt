package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.creationComment

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.creationComment.iCreationComment.ICreationCommentServiceCommentInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.comments.CreationCommentPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.serviceComment.InsertServiceCommentCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.ServiceComment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.ServiceCommentRepository

class CreationCommentServiceCommentInteractor
    (
    private val serviceCommentRepository: ServiceCommentRepository) :
    ICreationCommentServiceCommentInteractor, InsertServiceCommentCallback {

    private var rating = 0.0
    private var review = ""

    private lateinit var creationCommentPresenterCallback: CreationCommentPresenterCallback

    override fun setRating(rating: Double) {
        this.rating = rating
    }

    override fun setReview(review: String) {
        this.review = review
    }

    override fun createServiceComment(
        serviceComment: ServiceComment,
        creationCommentPresenterCallback: CreationCommentPresenterCallback
    ) {
        this.creationCommentPresenterCallback = creationCommentPresenterCallback
        serviceCommentRepository.insert(serviceComment, this)
    }

    override fun returnCreatedCallback(obj: ServiceComment) {
        creationCommentPresenterCallback.updateServiceCommentMessage(obj)
    }

}