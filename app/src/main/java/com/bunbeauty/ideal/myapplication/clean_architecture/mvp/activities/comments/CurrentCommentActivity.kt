package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.comments

import android.content.Intent
import android.os.Bundle
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.business.commets.current_comment.CurrentCommentCommentInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.ServiceComment
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.UserComment
import com.bunbeauty.ideal.myapplication.clean_architecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.profile.ProfileActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.base.BaseActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.comments.CurrentCommentPresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.comments.CurrentCommentView
import kotlinx.android.synthetic.main.activity_current_comment.*
import javax.inject.Inject

class CurrentCommentActivity : BaseActivity(), CurrentCommentView {

    @Inject
    lateinit var currentCommentCommentInteractor: CurrentCommentCommentInteractor

    @InjectPresenter
    lateinit var currentCommentPresenter: CurrentCommentPresenter

    @ProvidePresenter
    internal fun currentCommentPresenter(): CurrentCommentPresenter {
        buildDagger().inject(this)
        return CurrentCommentPresenter(
            currentCommentCommentInteractor
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current_comment)
        currentCommentPresenter.createCurrentCommentScreen()
    }

    override fun onResume() {
        super.onResume()
        initBottomPanel()
    }

    override fun setUserComment(userComment: UserComment) {
        initTopPanel(
            "${userComment.user.name}  ${userComment.user.surname}",
            ButtonTask.GO_TO_PROFILE,
            userComment.user.photoLink
        )
        activity_current_comment_tv_review.text = userComment.review
        activity_current_comment_rb_rating.rating = userComment.rating
    }

    override fun actionClick() {
        goToProfile()
    }

    override fun setServiceComment(serviceComment: ServiceComment) {
        initTopPanel(
            "${serviceComment.user.name}  ${serviceComment.user.surname}",
            ButtonTask.GO_TO_PROFILE,
            serviceComment.user.photoLink
        )
        activity_current_comment_tv_review.text = serviceComment.review
        activity_current_comment_rb_rating.rating = serviceComment.rating
    }

    fun goToProfile() {
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra(User.USER, currentCommentPresenter.getUserFromComment())
        startActivity(intent)
        overridePendingTransition(0, 0)
    }
}
