package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.comments

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.CreationCommentCommentInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.comments.CreationCommentPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Comment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.comments.CreationCommentView

@InjectViewState
class CreationCommentPresenter(private val creationCommentCommentInteractor: CreationCommentCommentInteractor) :
    MvpPresenter<CreationCommentView>(), CreationCommentPresenterCallback {

    fun createComment(rating: Double, review: String) {
        val comment = Comment()
        comment.rating = rating
        comment.review = review
        comment.ownerId = User.getMyId()
        creationCommentCommentInteractor.createComment(comment, this)
    }

    override fun showCommentCreated() {
        viewState.showCommentCreated()
    }

}