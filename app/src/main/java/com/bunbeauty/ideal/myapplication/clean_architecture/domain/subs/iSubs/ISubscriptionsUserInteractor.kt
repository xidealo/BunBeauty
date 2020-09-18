package com.bunbeauty.ideal.myapplication.clean_architecture.domain.subs.iSubs

import android.content.Intent
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subs.SubscriptionsPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Subscription

interface ISubscriptionsUserInteractor {
    fun createSubscriptionScreen(
        intent: Intent,
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