package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.comments

import android.os.Bundle
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.CreationCommentCommentInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.comments.CreationCommentPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.comments.CreationCommentView
import javax.inject.Inject

class CreationCommentActivity : MvpAppCompatActivity(), CreationCommentView {

    @Inject
    lateinit var creationCommentCommentInteractor: CreationCommentCommentInteractor

    @InjectPresenter
    lateinit var creationCommentPresenter: CreationCommentPresenter

    @ProvidePresenter
    internal fun provideProfilePresenter(): CreationCommentPresenter {
        DaggerAppComponent.builder()
            .appModule(AppModule(application, intent))
            .build()
            .inject(this)
        return CreationCommentPresenter(
            creationCommentCommentInteractor
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creation_comment)
    }
}
