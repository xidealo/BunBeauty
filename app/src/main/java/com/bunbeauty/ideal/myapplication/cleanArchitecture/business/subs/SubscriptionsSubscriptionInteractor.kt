package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.subs

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.subs.iSubs.ISubscriptionsSubscriptionInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subs.SubscriptionsPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.subscription.DeleteSubscriptionCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.subscription.SubscriptionsCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Subscription
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.SubscriptionRepository

class SubscriptionsSubscriptionInteractor(
    private val subscriptionRepository: SubscriptionRepository
) :
    ISubscriptionsSubscriptionInteractor, SubscriptionsCallback, DeleteSubscriptionCallback {

    private lateinit var subscriptionsPresenterCallback: SubscriptionsPresenterCallback
    private var cacheSubscriptions = mutableListOf<Subscription>()

    override fun getSubscriptionsLink() = cacheSubscriptions

    override fun getSubscriptions(
        user: User,
        subscriptionsPresenterCallback: SubscriptionsPresenterCallback
    ) {
        this.subscriptionsPresenterCallback = subscriptionsPresenterCallback
        subscriptionRepository.getByUserId(user.id, this)
    }

    override fun deleteSubscription(
        subscription: Subscription,
        subscriptionsPresenterCallback: SubscriptionsPresenterCallback
    ) {
        subscriptionRepository.delete(subscription, this)
    }

    override fun returnList(objects: List<Subscription>) {
        cacheSubscriptions.addAll(objects)
        subscriptionsPresenterCallback.getUsersBySubscription(objects)
    }

    override fun returnDeletedCallback(obj: Subscription) {
        cacheSubscriptions.remove(obj)
        subscriptionsPresenterCallback.showSubscriptions()
    }

}
