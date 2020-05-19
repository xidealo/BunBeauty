package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.comments

import android.os.Bundle
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.CurrentCommentInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.CurrentCommentPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.CurrentCommentView
import javax.inject.Inject

class CurrentCommentActivity : MvpAppCompatActivity(), CurrentCommentView {

    @Inject
    lateinit var currentCommentInteractor: CurrentCommentInteractor

    @InjectPresenter
    lateinit var currentCommentPresenter: CurrentCommentPresenter

    @ProvidePresenter
    internal fun currenCommentPresenter(): CurrentCommentPresenter {
        DaggerAppComponent
            .builder()
            .appModule(AppModule(application, intent))
            .build().inject(this)
        return CurrentCommentPresenter(currentCommentInteractor)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}