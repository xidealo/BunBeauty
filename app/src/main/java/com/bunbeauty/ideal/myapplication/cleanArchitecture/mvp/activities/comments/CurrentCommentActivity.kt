package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.comments

import android.os.Bundle
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.CurrentCommentInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.logIn.CurrentCommentPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.CurrentCommentView
import javax.inject.Inject

class CurrentCommentActivity : MvpAppCompatActivity(), CurrentCommentView {

    @Inject
    lateinit var currentCommentInteractor: CurrentCommentInteractor

    @Inject
    lateinit var currentCommentPresenter: CurrentCommentPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }



}