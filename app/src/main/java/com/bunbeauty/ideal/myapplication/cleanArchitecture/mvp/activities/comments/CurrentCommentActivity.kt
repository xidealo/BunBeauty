package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.comments

import android.content.Intent
import android.os.Bundle
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.CircularTransformation
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.currentComment.CurrentCommentCommentInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.ServiceComment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.UserComment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.profile.ProfileActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.comments.CurrentCommentPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.comments.CurrentCommentView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_current_comment.*
import javax.inject.Inject

class CurrentCommentActivity : MvpAppCompatActivity(),
    CurrentCommentView {

    @Inject
    lateinit var currentCommentCommentInteractor: CurrentCommentCommentInteractor

    @InjectPresenter
    lateinit var currentCommentPresenter: CurrentCommentPresenter

    @ProvidePresenter
    internal fun currenCommentPresenter(): CurrentCommentPresenter {
        DaggerAppComponent
            .builder()
            .appModule(AppModule(application, intent))
            .build().inject(this)
        return CurrentCommentPresenter(
            currentCommentCommentInteractor
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current_comment)
        currentCommentPresenter.createCurrentCommentScreen()
    }

    override fun setUserComment(userComment: UserComment) {
        Picasso.get()
            .load(userComment.user.photoLink)
            .resize(65, 65)
            .centerCrop()
            .transform(CircularTransformation())
            .into(avatarCurrentCommentActivityImage)

        userNameCurrentCommentActivityText.text =
            "${userComment.user.name}  ${userComment.user.surname}"
        reviewCurrentCommentActivityText.text = userComment.review
        ratingCurrentCommentActivityBar.rating = userComment.rating

        avatarCurrentCommentActivityImage.setOnClickListener {
            goToProfile()
        }
    }

    override fun setServiceComment(serviceComment: ServiceComment) {

    }

    fun goToProfile() {
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra(User.USER, currentCommentPresenter.getUserFromComment())
        startActivity(intent)
        overridePendingTransition(0, 0)
    }
}
