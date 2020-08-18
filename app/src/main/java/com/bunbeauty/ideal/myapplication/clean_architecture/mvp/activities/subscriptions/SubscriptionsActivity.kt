package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.subscriptions

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.adapters.SubscriptionAdapter
import com.bunbeauty.ideal.myapplication.clean_architecture.business.subs.SubscriptionsSubscriberInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.business.subs.SubscriptionsSubscriptionInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.business.subs.SubscriptionsUserInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Subscription
import com.bunbeauty.ideal.myapplication.clean_architecture.di.AppModule
import com.bunbeauty.ideal.myapplication.clean_architecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.clean_architecture.di.FirebaseModule
import com.bunbeauty.ideal.myapplication.clean_architecture.di.InteractorModule
import com.bunbeauty.ideal.myapplication.clean_architecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.interfaces.IBottomPanel
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.interfaces.ITopPanel
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.SubscriptionsPresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.SubscriptionsView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_subscriptions.*
import javax.inject.Inject

class SubscriptionsActivity : MvpAppCompatActivity(), ITopPanel, IBottomPanel, SubscriptionsView {

    override var panelContext: Activity = this

    @Inject
    lateinit var subscriptionsSubscriptionInteractor: SubscriptionsSubscriptionInteractor

    @Inject
    lateinit var subscriptionsUserInteractor: SubscriptionsUserInteractor

    @Inject
    lateinit var subscriptionsSubscriberInteractor: SubscriptionsSubscriberInteractor

    @Inject
    lateinit var subscriptionAdapter: SubscriptionAdapter

    @InjectPresenter
    lateinit var subscriptionsPresenter: SubscriptionsPresenter

    @ProvidePresenter
    internal fun provideProfilePresenter(): SubscriptionsPresenter {
        DaggerAppComponent.builder()
            .appModule(AppModule(application))
            .firebaseModule(FirebaseModule())
            .interactorModule(InteractorModule(intent))
            .build()
            .inject(this)
        return SubscriptionsPresenter(
            subscriptionsSubscriptionInteractor,
            subscriptionsUserInteractor,
            subscriptionsSubscriberInteractor
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscriptions)
        init()
        createPanels()
        hideEmptySubscriptions()
        subscriptionsPresenter.createSubscriptionsScreen()
    }

    override fun onResume() {
        super.onResume()
        initBottomPanel()
    }

    fun init() {
        resultsSubscribersRecycleView.layoutManager = LinearLayoutManager(this)
        resultsSubscribersRecycleView.adapter = subscriptionAdapter
    }

    private fun createPanels() {
        initTopPanel("Подписки", ButtonTask.NONE)
    }

    override fun showSubscriptions(subscriptions: List<Subscription>) {
        subscriptionAdapter.setData(subscriptions, subscriptionsPresenter)
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

    override fun showMessage(message: String) {
        Snackbar.make(subscriptionsMainLayout, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(this, R.color.mainBlue))
            .setActionTextColor(ContextCompat.getColor(this, R.color.white)).show()
    }

}