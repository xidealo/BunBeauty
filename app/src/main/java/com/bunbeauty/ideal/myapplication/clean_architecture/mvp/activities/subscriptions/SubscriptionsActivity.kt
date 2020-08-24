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
import com.bunbeauty.ideal.myapplication.clean_architecture.di.component.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.clean_architecture.di.module.AppModule
import com.bunbeauty.ideal.myapplication.clean_architecture.di.module.FirebaseModule
import com.bunbeauty.ideal.myapplication.clean_architecture.di.module.InteractorModule
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
        activity_subscriptions_rv_results.layoutManager = LinearLayoutManager(this)
        activity_subscriptions_rv_results.adapter = subscriptionAdapter
        subscriptionAdapter.setData(subscriptionsPresenter)
    }

    private fun createPanels() {
        initTopPanel("Подписки", ButtonTask.NONE)
    }

    override fun showSubscription(subscription: Subscription) {
        subscriptionAdapter.addItem(subscription)
    }

    override fun removeSubscription(subscription: Subscription) {
        subscriptionAdapter.removeItem(subscription)
    }

    override fun hideLoading() {
        activity_subscriptions_pb_loading.visibility = View.GONE
    }

    override fun showLoading() {
        activity_subscriptions_pb_loading.visibility = View.VISIBLE
    }

    override fun showEmptySubscriptions() {
        activity_subscriptions_tv_empty.visibility = View.VISIBLE
    }

    override fun hideEmptySubscriptions() {
        activity_subscriptions_tv_empty.visibility = View.GONE
    }

    override fun showMessage(message: String) {
        Snackbar.make(activity_subscriptions_ll_main, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(this, R.color.mainBlue))
            .setActionTextColor(ContextCompat.getColor(this, R.color.white)).show()
    }

}