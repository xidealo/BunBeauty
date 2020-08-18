package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.comments

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.business.commets.user_comments.iUserComments.IUserCommentsUserCommentInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.business.commets.user_comments.iUserComments.IUserCommentsUserInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.comments.UserCommentsPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.UserComment
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.comments.UserCommentsView

@InjectViewState
class UserCommentsPresenter(
    private val userCommentsUserCommentInteractor: IUserCommentsUserCommentInteractor,
    private val userCommentsUserInteractor: IUserCommentsUserInteractor
) :
    MvpPresenter<UserCommentsView>(), UserCommentsPresenterCallback {

    fun createUserCommentsScreen() {
        userCommentsUserCommentInteractor.getUserComments(
            userCommentsUserInteractor.getCurrentUser(),
            this
        )
    }

    override fun getUser(userComment: UserComment) {
        userCommentsUserInteractor.getUsers(userComment, this)
    }

    override fun setUserOnUserComment(user: User) {
        userCommentsUserCommentInteractor.setUserOnUserComment(user, this)
    }

    override fun updateUserComments(userComments: List<UserComment>) {
        viewState.hideLoading()
        viewState.updateUserComments(userComments)
    }

    override fun showEmptyScreen() {
        viewState.hideLoading()
        viewState.showEmptyScreen()
    }

}