package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.comments

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.serviceComments.iServiceComments.IServiceCommentsServiceCommentInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.serviceComments.iServiceComments.IServiceCommentsServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.serviceComments.iServiceComments.IServiceCommentsUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.comments.ServiceCommentsPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.ServiceComment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.UserComment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.comments.ServiceCommentsView

@InjectViewState
class ServiceCommentsPresenter(
    private val serviceCommentsServiceCommentInteractor: IServiceCommentsServiceCommentInteractor,
    private val serviceCommentsUserInteractor: IServiceCommentsUserInteractor,
    private val serviceCommentsServiceInteractor: IServiceCommentsServiceInteractor
) : MvpPresenter<ServiceCommentsView>(), ServiceCommentsPresenterCallback {

    fun createServiceCommentsScreen() {
        serviceCommentsServiceCommentInteractor.createServiceCommentsScreen(
            serviceCommentsServiceInteractor.getService(),
            this
        )
    }

    override fun getUser(serviceComment: ServiceComment) {
        serviceCommentsUserInteractor.getUsers(serviceComment, this)
    }

    override fun setUserOnServiceComment(user: User) {
        serviceCommentsServiceCommentInteractor.setUserOnServiceComment(user, this)
    }

    override fun updateServiceComments(serviceComments: List<ServiceComment>) {
        viewState.updateServiceComments(serviceComments)
    }

    override fun showEmptyScreen() {
        viewState.showEmptyScreen()
    }

}