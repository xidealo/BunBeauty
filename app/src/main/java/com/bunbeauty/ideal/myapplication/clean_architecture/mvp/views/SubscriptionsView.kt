package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views

import com.arellomobile.mvp.MvpView
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Subscription

interface SubscriptionsView : MvpView {
    fun showSubscription(subscription: Subscription)
    fun removeSubscription(subscription: Subscription)
    fun hideLoading()
    fun showLoading()
    fun showEmptySubscriptions()
    fun hideEmptySubscriptions()
    fun showMessage(message: String)
}