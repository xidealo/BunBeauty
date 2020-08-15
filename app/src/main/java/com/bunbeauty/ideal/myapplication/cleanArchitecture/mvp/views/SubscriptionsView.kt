package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views

import com.arellomobile.mvp.MvpView
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Subscription

interface SubscriptionsView : MvpView {
    fun showSubscriptions(subscriptions: List<Subscription>)
    fun hideLoading()
    fun showLoading()
    fun showEmptySubscriptions()
    fun hideEmptySubscriptions()
    fun showMessage(message: String)
}