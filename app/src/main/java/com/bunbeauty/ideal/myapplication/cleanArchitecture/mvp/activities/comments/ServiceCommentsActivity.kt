package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.comments

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.serviceComments.ServiceCommentsServiceCommentInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.serviceComments.ServiceCommentsUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.FirebaseModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.InteractorModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.IBottomPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.ITopPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.comments.ServiceCommentsPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.comments.ServiceCommentsView
import kotlinx.android.synthetic.main.activity_service_comments.*
import javax.inject.Inject

class ServiceCommentsActivity : MvpAppCompatActivity(), ServiceCommentsView, ITopPanel,
    IBottomPanel {

    override var panelContext: Activity = this

    @Inject
    lateinit var serviceCommentsServiceCommentInteractor: ServiceCommentsServiceCommentInteractor

    @Inject
    lateinit var serviceCommentsUserInteractor: ServiceCommentsUserInteractor

    @InjectPresenter
    lateinit var serviceCommentsPresenter: ServiceCommentsPresenter

    @ProvidePresenter
    fun commentsPresenter(): ServiceCommentsPresenter {
        DaggerAppComponent.builder()
            .appModule(AppModule(application))
            .firebaseModule(FirebaseModule())
            .interactorModule(InteractorModule(intent))
            .build()
            .inject(this)
        return ServiceCommentsPresenter(
            serviceCommentsServiceCommentInteractor,
            serviceCommentsUserInteractor
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_comments)
        init()
        initTopPanel("Оценки услуги", ButtonTask.NONE)
        initBottomPanel()
        hideEmptyScreen()
        //userCommentsPresenter.createUserCommentsScreen()
    }

    fun init() {
        resultsServiceCommentsRecycleView.layoutManager = LinearLayoutManager(this)
        //userCommentAdapter = UserCommentAdapter(userCommentsPresenter.getUserCommentsLink())
        //resultsServiceCommentsRecycleView.adapter = userCommentAdapter
    }


    override fun showLoading() {
        progressBarServiceComments.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progressBarServiceComments.visibility = View.GONE
    }

    override fun updateServiceComments() {
        //ServiceCommentAdapter.notifyDataSetChanged()
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
