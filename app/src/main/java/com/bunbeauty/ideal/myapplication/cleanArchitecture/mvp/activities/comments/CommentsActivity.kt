package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.comments

import android.os.Bundle
import com.arellomobile.mvp.MvpAppCompatActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.CommentsInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.CommentsPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.CommentsView
import javax.inject.Inject

class CommentsActivity : MvpAppCompatActivity(), CommentsView {

    @Inject
    lateinit var commentsInteractor: CommentsInteractor

    @Inject
    lateinit var commentsPresenter: CommentsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

}