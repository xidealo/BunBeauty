package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.comments

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters.ServiceCommentAdapter
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.api.gone
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.api.visible
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.commets.service_comments.ServiceCommentsServiceCommentInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.commets.service_comments.ServiceCommentsServiceInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.commets.service_comments.ServiceCommentsUserInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.ServiceComment
import com.bunbeauty.ideal.myapplication.clean_architecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.base.BaseActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.comments.ServiceCommentsPresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.comments.ServiceCommentsView
import kotlinx.android.synthetic.main.activity_service_comments.*
import javax.inject.Inject

class ServiceCommentsActivity : BaseActivity(), ServiceCommentsView {

    @Inject
    lateinit var serviceCommentAdapter: ServiceCommentAdapter

    @Inject
    lateinit var serviceCommentsServiceCommentInteractor: ServiceCommentsServiceCommentInteractor

    @Inject
    lateinit var serviceCommentsUserInteractor: ServiceCommentsUserInteractor

    @Inject
    lateinit var serviceCommentsServiceInteractor: ServiceCommentsServiceInteractor

    @InjectPresenter
    lateinit var serviceCommentsPresenter: ServiceCommentsPresenter

    @ProvidePresenter
    fun commentsPresenter(): ServiceCommentsPresenter {
        buildDagger().inject(this)
        return ServiceCommentsPresenter(
            serviceCommentsServiceCommentInteractor,
            serviceCommentsUserInteractor,
            serviceCommentsServiceInteractor
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_comments)
        init()
        initTopPanel("Оценки услуги", ButtonTask.NONE)
        initBottomPanel()
        hideEmptyScreen()
        serviceCommentsPresenter.createServiceCommentsScreen(15)
    }

    fun init() {
        activity_servicecomments_rv_results.layoutManager = LinearLayoutManager(this)
        activity_servicecomments_rv_results.adapter = serviceCommentAdapter
    }

    override fun showLoading() {
        activity_servicecomments_pb_loading.visible()
    }

    override fun hideLoading() {
        activity_servicecomments_pb_loading.gone()
    }

    override fun updateServiceComments(serviceComment: ServiceComment) {
        serviceCommentAdapter.addItem(serviceComment)
    }

    override fun showServiceComments() {
        activity_servicecomments_rv_results.visible()
    }

    override fun hideServiceComments() {
        activity_servicecomments_rv_results.gone()
    }

    override fun showEmptyScreen() {
        activity_servicecomments_tv_empty.visible()
    }

    override fun hideEmptyScreen() {
        activity_servicecomments_tv_empty.gone()
    }
}
