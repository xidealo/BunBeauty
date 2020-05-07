package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.subs.iSubs

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subs.SubscriptionsPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Subscription
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface ISubscriptionsSubscriptionInteractor {
    fun getSubscriptions(user: User, subscriptionsPresenterCallback: SubscriptionsPresenterCallback)
    fun getSubscriptionsLink():List<Subscription>
    fun deleteSubscription(
        subscription: Subscription,
        subscriptionsPresenterCallback: SubscriptionsPresenterCallback
    )

}