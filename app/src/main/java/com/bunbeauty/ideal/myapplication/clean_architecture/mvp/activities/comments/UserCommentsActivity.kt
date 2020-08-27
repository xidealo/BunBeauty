package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.comments

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.adapters.UserCommentAdapter
import com.bunbeauty.ideal.myapplication.clean_architecture.business.commets.user_comments.UserCommentsUserCommentInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.business.commets.user_comments.UserCommentsUserInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.UserComment
import com.bunbeauty.ideal.myapplication.clean_architecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.base.BaseActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.comments.UserCommentsPresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.comments.UserCommentsView
import kotlinx.android.synthetic.main.activity_comments.*
import javax.inject.Inject

class UserCommentsActivity : BaseActivity(), UserCommentsView {

    @Inject
    lateinit var userCommentAdapter: UserCommentAdapter

    @Inject
    lateinit var userCommentsCommentInteractor: UserCommentsUserCommentInteractor

    @Inject
    lateinit var userCommentsUserInteractor: UserCommentsUserInteractor

    @InjectPresenter
    lateinit var userCommentsPresenter: UserCommentsPresenter

    @ProvidePresenter
    internal fun commentsPresenter(): UserCommentsPresenter {
        buildDagger().inject(this)
        return UserCommentsPresenter(
            userCommentsCommentInteractor,
            userCommentsUserInteractor
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)
        init()
        initTopPanel("Оценки пользователя", ButtonTask.NONE)
        initBottomPanel()
        hideEmptyScreen()

        userCommentsPresenter.createUserCommentsScreen()
    }

    fun init() {
        activity_usercomments_rv_results.layoutManager = LinearLayoutManager(this)
        activity_usercomments_rv_results.adapter = userCommentAdapter
    }

    override fun showLoading() {
        activity_usercomments_pb_loading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        activity_usercomments_pb_loading.visibility = View.GONE
    }

    override fun updateUserComments(userComment: UserComment) {
        userCommentAdapter.addItem(userComment)
    }

    override fun showUserComments() {
        activity_usercomments_rv_results.visibility = View.VISIBLE
    }

    override fun hideUserComments() {
        activity_usercomments_rv_results.visibility = View.GONE
    }

    override fun showEmptyScreen() {
        activity_usercomments_tv_empty.visibility = View.VISIBLE
    }

    override fun hideEmptyScreen() {
        activity_usercomments_tv_empty.visibility = View.GONE
    }

}
