package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.comments

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.userComments.iUserComments.IUserCommentsUserCommentInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.userComments.iUserComments.IUserCommentsUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.comments.UserCommentsPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.UserComment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.comments.UserCommentsView

@InjectViewState
class UserCommentsPresenter(
    private val userCommentsUserCommentInteractor: IUserCommentsUserCommentInteractor,
    private val userCommentsUserInteractor: IUserCommentsUserInteractor
) :
    MvpPresenter<UserCommentsView>(), UserCommentsPresenterCallback {

    fun getUserCommentsLink() = userCommentsUserCommentInteractor.getUserCommentsLink()

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