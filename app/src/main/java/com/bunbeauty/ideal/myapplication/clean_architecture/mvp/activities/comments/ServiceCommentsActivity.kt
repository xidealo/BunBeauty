package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.comments

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.adapters.ServiceCommentAdapter
import com.bunbeauty.ideal.myapplication.clean_architecture.business.commets.service_comments.ServiceCommentsServiceCommentInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.business.commets.service_comments.ServiceCommentsServiceInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.business.commets.service_comments.ServiceCommentsUserInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.ServiceComment
import com.bunbeauty.ideal.myapplication.clean_architecture.di.component.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.clean_architecture.di.module.AdapterModule
import com.bunbeauty.ideal.myapplication.clean_architecture.di.module.AppModule
import com.bunbeauty.ideal.myapplication.clean_architecture.di.module.FirebaseModule
import com.bunbeauty.ideal.myapplication.clean_architecture.di.module.InteractorModule
import com.bunbeauty.ideal.myapplication.clean_architecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.interfaces.IBottomPanel
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.interfaces.ITopPanel
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.comments.ServiceCommentsPresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.comments.ServiceCommentsView
import kotlinx.android.synthetic.main.activity_service_comments.*
import javax.inject.Inject

class ServiceCommentsActivity : MvpAppCompatActivity(), ServiceCommentsView, ITopPanel,
    IBottomPanel {

    override var panelContext: Activity = this

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
        DaggerAppComponent.builder()
            .appModule(AppModule(application))
            .firebaseModule(FirebaseModule())
            .interactorModule(InteractorModule(intent))
            .adapterModule(AdapterModule())
            .build()
            .inject(this)
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
        serviceCommentsPresenter.createServiceCommentsScreen()
    }

    fun init() {
        activity_servicecomments_rv_results.layoutManager = LinearLayoutManager(this)
        activity_servicecomments_rv_results.adapter = serviceCommentAdapter
    }

    override fun showLoading() {
        activity_servicecomments_pb_loading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        activity_servicecomments_pb_loading.visibility = View.GONE
    }

    override fun updateServiceComments(serviceComment: ServiceComment) {
        serviceCommentAdapter.addItem(serviceComment)
    }

    override fun showServiceComments() {
        activity_servicecomments_rv_results.visibility = View.VISIBLE
    }

    override fun hideServiceComments() {
        activity_servicecomments_rv_results.visibility = View.GONE
    }

    override fun showEmptyScreen() {
        activity_servicecomments_tv_empty.visibility = View.VISIBLE
    }

    override fun hideEmptyScreen() {
        activity_servicecomments_tv_empty.visibility = View.GONE
    }
}
