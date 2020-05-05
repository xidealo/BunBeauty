package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.subs

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.subs.iSubs.ISubscriptionSubscriberInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subs.SubscriptionsPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.subscription.SubscriptionsCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Subscription
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.SubscriptionRepository

class SubscriptionsSubscriptionInteractor(private val subscriptionRepository: SubscriptionRepository) :
    ISubscriptionSubscriberInteractor, SubscriptionsCallback {

    private lateinit var subscriptionsPresenterCallback: SubscriptionsPresenterCallback
    override fun createSubscriptionScreen(subscriptionsPresenterCallback: SubscriptionsPresenterCallback) {
        this.subscriptionsPresenterCallback = subscriptionsPresenterCallback
        subscriptionRepository.getByUserId(User.getMyId(), this)
    }

    override fun returnList(objects: List<Subscription>) {
        subscriptionsPresenterCallback.getUserBySubscriptionId(objects)
    }

}
