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
import com.bunbeauty.ideal.myapplication.clean_architecture.di.*
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
        resultsServiceCommentsRecycleView.layoutManager = LinearLayoutManager(this)
        resultsServiceCommentsRecycleView.adapter = serviceCommentAdapter
    }

    override fun showLoading() {
        progressBarServiceComments.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progressBarServiceComments.visibility = View.GONE
    }

    override fun updateServiceComments(serviceComments: List<ServiceComment>) {
        serviceCommentAdapter.setData(serviceComments)
    }

    override fun showServiceComments() {
        resultsServiceCommentsRecycleView.visibility = View.VISIBLE
    }

    override fun hideServiceComments() {
        resultsServiceCommentsRecycleView.visibility = View.GONE
    }

    override fun showEmptyScreen() {
        noServiceCommentsText.visibility = View.VISIBLE
    }

    override fun hideEmptyScreen() {
        noServiceCommentsText.visibility = View.GONE
    }
}
