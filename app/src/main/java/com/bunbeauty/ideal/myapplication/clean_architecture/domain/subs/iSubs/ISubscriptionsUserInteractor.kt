package com.bunbeauty.ideal.myapplication.clean_architecture.domain.subs.iSubs

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subs.SubscriptionsPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Subscription

interface ISubscriptionsUserInteractor {
    fun createSubscriptionScreen(
        loadingLimit: Int,
        subscriptionsPresenterCallback: SubscriptionsPresenterCallback
    )

    fun getUsersBySubscription(
        subscription: Subscription,
        subscriptionsPresenterCallback: SubscriptionsPresenterCallback
    )

    fun deleteUser(
        subscriptionId: String,
        subscriptionsPresenterCallback: SubscriptionsPresenterCallback
    )
}