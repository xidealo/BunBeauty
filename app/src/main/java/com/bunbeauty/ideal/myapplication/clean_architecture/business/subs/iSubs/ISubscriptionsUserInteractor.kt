package com.bunbeauty.ideal.myapplication.clean_architecture.business.subs.iSubs

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subs.SubscriptionsPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Subscription
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User

interface ISubscriptionsUserInteractor {
    fun createSubscriptionScreen(subscriptionsPresenterCallback: SubscriptionsPresenterCallback)

    fun getUsersBySubscription(
        subscriptions: List<Subscription>,
        subscriptionsPresenterCallback: SubscriptionsPresenterCallback
    )

    fun getUsersLink(): List<User>

    fun deleteUser(subscriptionId: String, subscriptionsPresenterCallback: SubscriptionsPresenterCallback)
}