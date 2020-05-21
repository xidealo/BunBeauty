package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.comments

import android.os.Bundle
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.CommentsInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.CommentsPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.CommentsView
import javax.inject.Inject

class CommentsActivity :  MvpAppCompatActivity(), CommentsView {

    @Inject
    lateinit var commentsInteractor: CommentsInteractor

    @InjectPresenter
    lateinit var commentsPresenter: CommentsPresenter

    @ProvidePresenter
    internal fun commentsPresenter(): CommentsPresenter {
        DaggerAppComponent
            .builder()
            .appModule(AppModule(application, intent))
            .build().inject(this)
        return CommentsPresenter(commentsInteractor)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)
    }
}
