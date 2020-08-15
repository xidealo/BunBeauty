package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subs

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Subscription
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface SubscriptionsPresenterCallback {
    fun getSubscriptions(user: User)
    fun getUsersBySubscription(subscriptions: List<Subscription>)
    fun showSubscriptions(subscriptions: List<Subscription>)
    fun showDeletedSubscription(subscription: Subscription)
    fun deleteUser(subscriptionId: String)
    fun showEmptySubscriptions()
    fun fillSubscriptions(
        users: List<User>
    )
}