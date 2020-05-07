package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.subs.iSubs

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subs.SubscriptionsPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Subscription
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface ISubscriptionsUserInteractor {
    fun createSubscriptionScreen(subscriptionsPresenterCallback: SubscriptionsPresenterCallback)

    fun getUsersBySubscription(
        subscriptions: List<Subscription>,
        subscriptionsPresenterCallback: SubscriptionsPresenterCallback
    )

    fun getUsersLink(): List<User>

    fun deleteUser(user: User, subscriptionsPresenterCallback: SubscriptionsPresenterCallback)
}