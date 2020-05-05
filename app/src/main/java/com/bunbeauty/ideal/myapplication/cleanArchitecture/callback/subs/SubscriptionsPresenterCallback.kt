package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subs

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Subscription

interface SubscriptionsPresenterCallback {
    fun getUserBySubscriptionId(subscriptions: List<Subscription>)
    fun showSubscriptions(subscriptions: List<Subscription>)
}