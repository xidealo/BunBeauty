package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.comments

import android.content.Intent
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.commets.current_comment.iCurrentComment.ICurrentCommentCommentInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.comments.CurrentCommentPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.ServiceComment
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.UserComment
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.comments.CurrentCommentView

@InjectViewState
class CurrentCommentPresenter(
    private val currentCommentCommentInteractor: ICurrentCommentCommentInteractor,
    private val intent: Intent
) :
    MvpPresenter<CurrentCommentView>(), CurrentCommentPresenterCallback {

    fun createCurrentCommentScreen() {
        currentCommentCommentInteractor.createCurrentCommentScreen(intent, this)
    }

    fun getUserFromComment() = currentCommentCommentInteractor.getUserFromComment()

    override fun setUserComment(userComment: UserComment) {
        viewState.setUserComment(userComment)
    }

    override fun setServiceComment(serviceComment: ServiceComment) {
        viewState.setServiceComment(serviceComment)
    }

}