package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.subscriptions

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.Tag
import com.bunbeauty.ideal.myapplication.clean_architecture.adapters.SubscriptionAdapter
import com.bunbeauty.ideal.myapplication.clean_architecture.business.api.gone
import com.bunbeauty.ideal.myapplication.clean_architecture.business.api.visible
import com.bunbeauty.ideal.myapplication.clean_architecture.business.subs.SubscriptionsSubscriberInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.business.subs.SubscriptionsSubscriptionInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.business.subs.SubscriptionsUserInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Subscription
import com.bunbeauty.ideal.myapplication.clean_architecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.base.BaseActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.SubscriptionsPresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.SubscriptionsView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_subscriptions.*
import javax.inject.Inject

class SubscriptionsActivity : BaseActivity(), SubscriptionsView {

    private var loadingLimit = 15
    private var isScrolling = false

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
        buildDagger().inject(this)

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
        subscriptionsPresenter.createSubscriptionsScreen(loadingLimit)
    }

    override fun onResume() {
        super.onResume()
        initBottomPanel()
    }

    fun init() {
        val linearLayoutManager = LinearLayoutManager(this)
        activity_subscriptions_rv_results.layoutManager = linearLayoutManager
        activity_subscriptions_rv_results.adapter = subscriptionAdapter
        subscriptionAdapter.setData(subscriptionsPresenter)

        activity_subscriptions_rv_results.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (isScrolling && dy > 30 && linearLayoutManager.findLastVisibleItemPosition() <= loadingLimit - 3) {
                    Log.d(Tag.TEST_TAG, "Запрос на докачку сообщений")
                    updateData()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }
            }
        })
    }

    fun updateData() {
        isScrolling = false
        loadingLimit += 15
        subscriptionsPresenter.createSubscriptionsScreen(loadingLimit)
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
        activity_subscriptions_pb_loading.gone()
    }

    override fun showLoading() {
        activity_subscriptions_pb_loading.visible()
    }

    override fun showEmptySubscriptions() {
        activity_subscriptions_tv_empty.visible()
    }

    override fun hideEmptySubscriptions() {
        activity_subscriptions_tv_empty.gone()
    }

    override fun showMessage(message: String) {
        Snackbar.make(activity_subscriptions_ll_main, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(this, R.color.mainBlue))
            .setActionTextColor(ContextCompat.getColor(this, R.color.white)).show()
    }

}