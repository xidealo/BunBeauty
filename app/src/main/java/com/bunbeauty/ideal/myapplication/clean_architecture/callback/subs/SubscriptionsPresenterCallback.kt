package com.bunbeauty.ideal.myapplication.clean_architecture.callback.subs

import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Subscription
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User

interface SubscriptionsPresenterCallback {
    fun getSubscriptions(user: User, loadingLimit: Int)
    fun getUserBySubscription(subscription: Subscription)
    fun showSubscription(subscription: Subscription)
    fun removeSubscription(subscription: Subscription)
    fun showDeletedSubscription(subscription: Subscription)
    fun deleteUser(subscriptionId: String)
    fun showEmptySubscription()
    fun fillSubscription(
        user: User
    )
}