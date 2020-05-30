package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.comments

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.creationComment.iCreationComment.ICreationCommentCommentInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.creationComment.iCreationComment.ICreationCommentMessageInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.creationComment.iCreationComment.ICreationCommentOrderInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.creationComment.iCreationComment.ICreationCommentServiceCommentInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.comments.CreationCommentPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Order
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.ServiceComment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.UserComment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.comments.CreationCommentView

@InjectViewState
class CreationCommentPresenter(
    private val creationCommentCommentInteractor: ICreationCommentCommentInteractor,
    private val creationCommentServiceCommentInteractor: ICreationCommentServiceCommentInteractor,
    private val creationCommentOrderInteractor: ICreationCommentOrderInteractor,
    private val creationCommentMessageInteractor: ICreationCommentMessageInteractor
) :
    MvpPresenter<CreationCommentView>(), CreationCommentPresenterCallback {

    fun checkMessage(rating: Float, review: String) {
        creationCommentMessageInteractor.checkMessage(rating, review, this)
    }

    override fun createUserComment(rating: Float, review: String) {
        val comment = UserComment()
        comment.rating = rating
        comment.review = review
        comment.ownerId = User.getMyId()
        creationCommentCommentInteractor.createUserComment(comment, this)
    }

    override fun getOrderForServiceComment(message: Message, rating: Float, review: String) {
        creationCommentServiceCommentInteractor.setRating(rating)
        creationCommentServiceCommentInteractor.setReview(review)
        creationCommentOrderInteractor.getOrderById(message, this)
    }

    override fun createServiceComment(order: Order) {
        val comment = ServiceComment()
        comment.userId = order.masterId
        comment.serviceId = order.serviceId
        comment.ownerId = User.getMyId()
        creationCommentServiceCommentInteractor.createServiceComment(comment, this)
    }

    override fun updateUserCommentMessage(userComment: UserComment) {
        creationCommentMessageInteractor.updateUserCommentMessage(userComment, this)
    }

    override fun updateServiceCommentMessage(serviceComment: ServiceComment) {
        creationCommentMessageInteractor.updateServiceCommentMessage(serviceComment, this)
    }

    override fun showCommentCreated(message: Message) {
        viewState.showCommentCreated(message)
    }

}