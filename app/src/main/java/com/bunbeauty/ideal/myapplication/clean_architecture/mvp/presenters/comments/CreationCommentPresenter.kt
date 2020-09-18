package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.comments

import android.content.Intent
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.commets.creation_comment.i_creation_comment.*
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.comments.CreationCommentPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Order
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.ServiceComment
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.UserComment
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.comments.CreationCommentView

@InjectViewState
class CreationCommentPresenter(
    private val creationCommentUserCommentInteractor: ICreationCommentUserCommentInteractor,
    private val creationCommentServiceCommentInteractor: ICreationCommentServiceCommentInteractor,
    private val creationCommentOrderInteractor: ICreationCommentOrderInteractor,
    private val creationCommentMessageInteractor: ICreationCommentMessageInteractor,
    private val creationCommentUserInteractor: ICreationCommentUserInteractor,
    private val creationCommentServiceInteractor: ICreationCommentServiceInteractor,
    private val intent: Intent
) : MvpPresenter<CreationCommentView>(), CreationCommentPresenterCallback {

    fun checkMessage(rating: Float, review: String) {
        creationCommentMessageInteractor.checkMessage(intent, rating, review, this)
    }

    override fun createUserComment(rating: Float, review: String) {
        val userComment = UserComment()
        userComment.rating = rating
        userComment.review = review
        userComment.ownerId = User.getMyId()
        creationCommentUserCommentInteractor.createUserComment(
            userComment,
            creationCommentUserInteractor.getUser(intent),
            this
        )
    }

    override fun getOrderForServiceComment(message: Message, rating: Float, review: String) {
        creationCommentServiceCommentInteractor.setRating(rating)
        creationCommentServiceCommentInteractor.setReview(review)
        creationCommentOrderInteractor.getOrderById(message, this)
    }

    override fun createServiceComment(order: Order) {
        val serviceComment = ServiceComment()
        serviceComment.userId = order.masterId
        serviceComment.serviceId = order.serviceId
        serviceComment.serviceName = order.serviceName
        serviceComment.ownerId = User.getMyId()
        creationCommentServiceCommentInteractor.createServiceComment(serviceComment, this)
    }

    override fun updateUserRating(userComment: UserComment) {
        creationCommentUserInteractor.updateUser(
            creationCommentUserInteractor.getUser(intent),
            userComment,
            this
        )
    }

    override fun updateServiceRating(serviceComment: ServiceComment) {
        creationCommentServiceInteractor.updateService(serviceComment, this)
    }

    override fun updateUserCommentMessage(userComment: UserComment) {
        creationCommentMessageInteractor.updateUserCommentMessage(intent, userComment, this)
    }

    override fun updateServiceCommentMessage(serviceComment: ServiceComment) {
        creationCommentMessageInteractor.updateServiceCommentMessage(intent, serviceComment, this)
    }

    override fun showCommentCreated(message: Message) {
        viewState.showCommentCreated(message, creationCommentUserInteractor.getUser(intent))
    }

}