package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.subscriptions

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.SubscriptionAdapter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.subs.SubscriptionsSubscriptionInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.subs.SubscriptionsUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.IBottomPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.ITopPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.SubscriptionsPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.SubscriptionsView
import javax.inject.Inject

class SubscriptionsActivity : MvpAppCompatActivity(), ITopPanel, IBottomPanel, SubscriptionsView {

    private lateinit var resultsSubscribersRecycleView: RecyclerView
    private lateinit var subscriptionAdapter: SubscriptionAdapter
    private lateinit var emptySubscriptionsSubscriptionsText: TextView
    private lateinit var progressBarSubscribers: ProgressBar

    override var bottomNavigationContext: Context = this

    @Inject
    lateinit var subscriptionsSubscriptionInteractor: SubscriptionsSubscriptionInteractor

    @Inject
    lateinit var subscriptionsUserInteractor: SubscriptionsUserInteractor

    @InjectPresenter
    lateinit var subscriptionsPresenter: SubscriptionsPresenter

    @ProvidePresenter
    internal fun provideProfilePresenter(): SubscriptionsPresenter {
        DaggerAppComponent.builder()
            .appModule(AppModule(application, intent))
            .build()
            .inject(this)
        return SubscriptionsPresenter(
            subscriptionsSubscriptionInteractor,
            subscriptionsUserInteractor
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.subscribers)
        init()
        createPanels()
        hideEmptySubscriptions()
        subscriptionsPresenter.createSubscriptionsScreen()
    }

    fun init() {
        resultsSubscribersRecycleView = findViewById(R.id.resultsSubscribersRecycleView)
        emptySubscriptionsSubscriptionsText = findViewById(R.id.emptySubscriptionsSubscriptionsText)
        progressBarSubscribers = findViewById(R.id.progressBarSubscribers)

        resultsSubscribersRecycleView.layoutManager = LinearLayoutManager(this)

        subscriptionAdapter = SubscriptionAdapter(subscriptionsPresenter.getUsersLink())
        resultsSubscribersRecycleView.adapter = subscriptionAdapter
    }

    private fun createPanels() {
        createTopPanel("Подписки", ButtonTask.NONE, supportFragmentManager)
        initBottomPanel()
    }

    override fun showSubscriptions(users: List<User>) {
        subscriptionAdapter.notifyDataSetChanged()
    }

    override fun hideLoading() {
        progressBarSubscribers.visibility = View.GONE
    }

    override fun showLoading() {
        progressBarSubscribers.visibility = View.VISIBLE
    }

    override fun showEmptySubscriptions() {
        emptySubscriptionsSubscriptionsText.visibility = View.VISIBLE
    }

    override fun hideEmptySubscriptions() {
        emptySubscriptionsSubscriptionsText.visibility = View.GONE
    }

}