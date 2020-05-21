package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.comments

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.CurrentCommentInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.comments.CurrentCommentView

@InjectViewState
class CurrentCommentPresenter(private val currentCommentPresenter: CurrentCommentInteractor) :
    MvpPresenter<CurrentCommentView>() {
}