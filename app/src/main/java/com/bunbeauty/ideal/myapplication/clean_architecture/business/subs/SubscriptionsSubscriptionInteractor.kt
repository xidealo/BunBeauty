package com.bunbeauty.ideal.myapplication.clean_architecture.business.subs

import com.bunbeauty.ideal.myapplication.clean_architecture.business.subs.iSubs.ISubscriptionsSubscriptionInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subs.SubscriptionsPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.subscription.DeleteSubscriptionCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.subscription.SubscriptionsCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Subscription
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.SubscriptionRepository

class SubscriptionsSubscriptionInteractor(
    private val subscriptionRepository: SubscriptionRepository
) : ISubscriptionsSubscriptionInteractor, SubscriptionsCallback, DeleteSubscriptionCallback {

    private lateinit var subscriptionsPresenterCallback: SubscriptionsPresenterCallback
    private var cacheSubscriptions = mutableListOf<Subscription>()

    override fun getSubscriptions(
        user: User,
        subscriptionsPresenterCallback: SubscriptionsPresenterCallback
    ) {
        this.subscriptionsPresenterCallback = subscriptionsPresenterCallback
        subscriptionRepository.getByUserId(user.id, this)
    }

    override fun returnList(objects: List<Subscription>) {
        if (objects.isEmpty()) {
            subscriptionsPresenterCallback.showEmptySubscription()
            return
        }
        cacheSubscriptions.addAll(objects)
        for (subscription in objects)
            subscriptionsPresenterCallback.getUserBySubscription(subscription)
    }

    override fun deleteSubscription(
        subscription: Subscription,
        subscriptionsPresenterCallback: SubscriptionsPresenterCallback
    ) {
        cacheSubscriptions.remove(subscription)
        subscriptionRepository.delete(subscription, this)
    }

    override fun fillSubscription(
        user: User,
        subscriptionsPresenterCallback: SubscriptionsPresenterCallback
    ) {
        val subscriptionWithUserId = cacheSubscriptions.find { it.subscriptionId == user.id }
        if (subscriptionWithUserId != null) {
            subscriptionWithUserId.subscriptionUser = user
            subscriptionsPresenterCallback.showSubscription(subscriptionWithUserId)
        }
    }

    override fun returnDeletedCallback(obj: Subscription) {
        cacheSubscriptions.remove(obj)
        subscriptionsPresenterCallback.deleteUser(obj.subscriptionId)
        subscriptionsPresenterCallback.showDeletedSubscription(obj)
        subscriptionsPresenterCallback.removeSubscription(obj)
    }

}
