package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.comments

import android.content.Intent
import android.os.Bundle
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.creationComment.CreationCommentMessageInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.creationComment.CreationCommentOrderInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.creationComment.CreationCommentServiceCommentInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.creationComment.CreationCommentUserCommentInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.comments.CreationCommentPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.comments.CreationCommentView
import kotlinx.android.synthetic.main.activity_creation_comment.*
import javax.inject.Inject

class CreationCommentActivity : MvpAppCompatActivity(), CreationCommentView {

    @Inject
    lateinit var creationCommentUserCommentInteractor: CreationCommentUserCommentInteractor

    @Inject
    lateinit var creationCommentMessageInteractor: CreationCommentMessageInteractor

    @Inject
    lateinit var creationCommentServiceCommentInteractor: CreationCommentServiceCommentInteractor

    @Inject
    lateinit var creationCommentOrderInteractor: CreationCommentOrderInteractor

    @InjectPresenter
    lateinit var creationCommentPresenter: CreationCommentPresenter

    @ProvidePresenter
    internal fun provideProfilePresenter(): CreationCommentPresenter {
        DaggerAppComponent.builder()
            .appModule(AppModule(application, intent))
            .build()
            .inject(this)
        return CreationCommentPresenter(
            creationCommentUserCommentInteractor,
            creationCommentServiceCommentInteractor,
            creationCommentOrderInteractor,
            creationCommentMessageInteractor
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creation_comment)
        configViews()
    }

    private fun configViews() {
        rateCreationCommentBtn.setOnClickListener {
            creationCommentPresenter.checkMessage(
                ratingCreationCommentRatingBar.rating.toDouble(),
                reviewCreationCommentInput.text.toString()
            )
        }
    }

    override fun showCommentCreated(message: Message) {
        val intent = Intent()
        intent.putExtra(Message.MESSAGE, message)
        setResult(RESULT_OK, intent)
        finish()
    }

}
