package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.comments

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.commets.service_comments.iServiceComments.IServiceCommentsServiceCommentInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.commets.service_comments.iServiceComments.IServiceCommentsServiceInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.commets.service_comments.iServiceComments.IServiceCommentsUserInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.comments.ServiceCommentsPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.ServiceComment
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.comments.ServiceCommentsView

@InjectViewState
class ServiceCommentsPresenter(
    private val serviceCommentsServiceCommentInteractor: IServiceCommentsServiceCommentInteractor,
    private val serviceCommentsUserInteractor: IServiceCommentsUserInteractor,
    private val serviceCommentsServiceInteractor: IServiceCommentsServiceInteractor
) : MvpPresenter<ServiceCommentsView>(), ServiceCommentsPresenterCallback {

    fun createServiceCommentsScreen(loadingLimit: Int) {
        serviceCommentsServiceCommentInteractor.createServiceCommentsScreen(
            serviceCommentsServiceInteractor.getService(),
            loadingLimit,
            this
        )
    }

    override fun getUser(serviceComment: ServiceComment) {
        serviceCommentsUserInteractor.getUsers(serviceComment, this)
    }

    override fun setUserOnServiceComment(user: User) {
        serviceCommentsServiceCommentInteractor.setUserOnServiceComment(user, this)
    }

    override fun updateServiceComments(serviceComment: ServiceComment) {
        viewState.hideLoading()
        viewState.updateServiceComments(serviceComment)
    }

    override fun showEmptyScreen() {
        viewState.hideLoading()
        viewState.showEmptyScreen()
    }

}