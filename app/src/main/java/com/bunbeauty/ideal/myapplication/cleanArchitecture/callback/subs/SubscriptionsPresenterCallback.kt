package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subs

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Subscription
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface SubscriptionsPresenterCallback {
    fun getSubscriptions(user: User)
    fun getUsersBySubscription(subscriptions: List<Subscription>)
    fun showSubscriptions(users: List<User>)
    fun showEmptySubscriptions()
}