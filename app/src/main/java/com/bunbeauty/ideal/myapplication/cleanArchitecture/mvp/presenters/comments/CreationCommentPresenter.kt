package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.comments

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.CreationCommentCommentInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.comments.CreationCommentView

@InjectViewState
class CreationCommentPresenter(private val creationCommentCommentInteractor: CreationCommentCommentInteractor) :
    MvpPresenter<CreationCommentView>() {


}