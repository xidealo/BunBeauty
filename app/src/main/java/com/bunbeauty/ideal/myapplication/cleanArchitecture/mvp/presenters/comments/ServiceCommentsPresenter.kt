package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.comments

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.serviceComments.iServiceComments.IServiceCommentsServiceCommentInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.serviceComments.iServiceComments.IServiceCommentsUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.comments.ServiceCommentsView

@InjectViewState
class ServiceCommentsPresenter(
    private val serviceCommentsServiceCommentInteractor: IServiceCommentsServiceCommentInteractor,
    private val serviceCommentsUserInteractor: IServiceCommentsUserInteractor
) :
    MvpPresenter<ServiceCommentsView>() {

}