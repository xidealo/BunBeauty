package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.comments

import android.content.Intent
import android.os.Bundle
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.business.CircularTransformation
import com.bunbeauty.ideal.myapplication.clean_architecture.business.commets.current_comment.CurrentCommentCommentInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.ServiceComment
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.UserComment
import com.bunbeauty.ideal.myapplication.clean_architecture.di.AppModule
import com.bunbeauty.ideal.myapplication.clean_architecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.clean_architecture.di.FirebaseModule
import com.bunbeauty.ideal.myapplication.clean_architecture.di.InteractorModule
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.profile.ProfileActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.comments.CurrentCommentPresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.comments.CurrentCommentView
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
        DaggerAppComponent.builder()
            .appModule(AppModule(application))
            .firebaseModule(FirebaseModule())
            .interactorModule(InteractorModule(intent))
            .build()
            .inject(this)
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
        Picasso.get()
            .load(serviceComment.user.photoLink)
            .resize(65, 65)
            .centerCrop()
            .transform(CircularTransformation())
            .into(avatarCurrentCommentActivityImage)

        userNameCurrentCommentActivityText.text =
            "${serviceComment.user.name}  ${serviceComment.user.surname}"
        reviewCurrentCommentActivityText.text = serviceComment.review
        ratingCurrentCommentActivityBar.rating = serviceComment.rating

        avatarCurrentCommentActivityImage.setOnClickListener {
            goToProfile()
        }
    }

    fun goToProfile() {
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra(User.USER, currentCommentPresenter.getUserFromComment())
        startActivity(intent)
        overridePendingTransition(0, 0)
    }
}
