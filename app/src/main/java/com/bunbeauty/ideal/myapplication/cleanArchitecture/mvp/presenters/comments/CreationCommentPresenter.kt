package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.comments

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.creationComment.iCreationComment.ICreationCommentCommentInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.creationComment.iCreationComment.ICreationCommentMessageInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.creationComment.iCreationComment.ICreationCommentServiceCommentInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.comments.CreationCommentPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.UserComment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.comments.CreationCommentView

@InjectViewState
class CreationCommentPresenter(
    private val creationCommentCommentInteractor: ICreationCommentCommentInteractor,
    private val creationCommentServiceCommentInteractor: ICreationCommentServiceCommentInteractor,
    private val creationCommentMessageInteractor: ICreationCommentMessageInteractor
) :
    MvpPresenter<CreationCommentView>(), CreationCommentPresenterCallback {

    fun createComment(rating: Double, review: String) {
        val comment = UserComment()
        comment.rating = rating
        comment.review = review
        comment.ownerId = User.getMyId()
        creationCommentCommentInteractor.createUserComment(comment, this)
    }

    override fun showCommentCreated(message: Message) {
        viewState.showCommentCreated(message)
    }

    override fun createServiceComment(rating: Double, review: String) {
        //по ордеру из сообщения сделать запрос, получить userId and serviceId и потом только создавать коммент
       /* val comment = ServiceComment()
        comment.rating = rating
        comment.review = review
        comment.ownerId = User.getMyId()
        creationCommentServiceCommentInteractor.createServiceComment()*/
    }

    override fun updateCommentMessage(userComment: UserComment) {
        creationCommentMessageInteractor.updateCommentMessage(userComment, this)
    }

}