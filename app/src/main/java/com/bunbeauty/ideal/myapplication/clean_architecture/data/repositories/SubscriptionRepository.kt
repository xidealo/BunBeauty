package com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.subscription.DeleteSubscriptionCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.subscription.InsertSubscriptionCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.subscription.SubscriptionsCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.subscription.UpdateSubscriptionCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.api.SubscriptionFirebase
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Subscription
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.ISubscriptionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SubscriptionRepository(private val subscriptionFirebase: SubscriptionFirebase) :
    BaseRepository(), ISubscriptionRepository {

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
        launch {
            subscriptionFirebase.getByUserId(userId, subscriptionsCallback)
        }
    }

    override fun getByUserId(
        userId: String,
        loadingLimit: Int,
        subscriptionsCallback: SubscriptionsCallback
    ) {
        launch {
            subscriptionFirebase.getByUserId(userId, loadingLimit, subscriptionsCallback)
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


}