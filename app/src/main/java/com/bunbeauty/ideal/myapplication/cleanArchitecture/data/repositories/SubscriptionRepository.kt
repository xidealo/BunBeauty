package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.subscription.DeleteSubscriptionCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.subscription.InsertSubscriptionCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.subscription.SubscriptionsCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.subscription.UpdateSubscriptionCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.SubscriptionFirebase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Subscription
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.ISubscriptionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SubscriptionRepository(private val subscriptionFirebase: SubscriptionFirebase) :
    BaseRepository(),
    ISubscriptionRepository, SubscriptionsCallback {

    private lateinit var subscriptionsCallback: SubscriptionsCallback

    override fun insert(
        subscription: Subscription,
        insertSubscriptionCallback: InsertSubscriptionCallback
    ) {
        launch {
            subscription.id = subscriptionFirebase.getIdForNew(subscription.userId)
            subscriptionFirebase.insert(subscription)
            withContext(Dispatchers.Main) {
                insertSubscriptionCallback.returnCreatedCallback(subscription)
            }
        }
    }

    override fun delete(
        subscription: Subscription,
        deleteSubscriptionCallback: DeleteSubscriptionCallback
    ) {
        launch {
            subscriptionFirebase.delete(subscription)
            withContext(Dispatchers.Main) {
                deleteSubscriptionCallback.returnDeletedCallback(subscription)
            }
        }
    }

    override fun update(
        subscription: Subscription,
        updateSubscriptionCallback: UpdateSubscriptionCallback
    ) {
        launch {

        }
    }

    override fun get(subscriptionsCallback: SubscriptionsCallback) {
        launch {

        }
    }

    override fun getByUserId(
        userId: String,
        subscriptionsCallback: SubscriptionsCallback
    ) {
        this.subscriptionsCallback = subscriptionsCallback
        launch {
            subscriptionFirebase.getByUserId(userId, subscriptionsCallback)
        }
    }

    override fun deleteByBySubscriptionId(
        subscription: Subscription,
        deleteSubscriptionCallback: DeleteSubscriptionCallback
    ) {
        launch {
            subscriptionFirebase.deleteByBySubscriptionId(subscription)
            withContext(Dispatchers.Main) {
                deleteSubscriptionCallback.returnDeletedCallback(subscription)
            }
        }
    }

    override fun returnList(objects: List<Subscription>) {
        subscriptionsCallback.returnList(objects)
    }

}