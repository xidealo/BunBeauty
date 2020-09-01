package com.bunbeauty.ideal.myapplication.clean_architecture.business.subs.iSubs

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subs.SubscriptionsPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Subscription
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User

interface ISubscriptionsSubscriptionInteractor {
    fun getSubscriptions(
        user: User,
        loadingLimit: Int,
        subscriptionsPresenterCallback: SubscriptionsPresenterCallback
    )

    fun deleteSubscription(
        subscription: Subscription,
        subscriptionsPresenterCallback: SubscriptionsPresenterCallback
    )

    fun fillSubscription(
        user: User, subscriptionsPresenterCallback: SubscriptionsPresenterCallback
    )
}