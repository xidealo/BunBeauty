package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.comments

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.CommentsInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.comments.CommentsView

@InjectViewState
class CommentsPresenter(private val commentsInteractor: CommentsInteractor) :
    MvpPresenter<CommentsView>(){
}