package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.comments

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.currentComment.iCurrentComment.ICurrentCommentCommentInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.comments.CurrentCommentPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.ServiceComment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.UserComment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.comments.CurrentCommentView

@InjectViewState
class CurrentCommentPresenter(
    private val currentCommentCommentInteractor: ICurrentCommentCommentInteractor
) :
    MvpPresenter<CurrentCommentView>(), CurrentCommentPresenterCallback {

    fun createCurrentCommentScreen() {
        currentCommentCommentInteractor.getComment(this)
    }

    override fun setUserComment(userComment: UserComment) {
        viewState.setUserComment(userComment)
    }

    override fun setServiceComment(serviceComment: ServiceComment) {
        viewState.setServiceComment(serviceComment)
    }

}