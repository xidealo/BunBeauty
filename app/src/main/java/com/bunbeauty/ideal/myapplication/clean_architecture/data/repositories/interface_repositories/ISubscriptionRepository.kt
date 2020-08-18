package com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.subscription.DeleteSubscriptionCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.subscription.InsertSubscriptionCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.subscription.SubscriptionsCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.subscription.UpdateSubscriptionCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Subscription

interface ISubscriptionRepository {
    fun insert(subscription: Subscription, insertSubscriptionCallback: InsertSubscriptionCallback)
    fun delete(subscription: Subscription, deleteSubscriptionCallback: DeleteSubscriptionCallback)
    fun update(subscription: Subscription, updateSubscriptionCallback: UpdateSubscriptionCallback)
    fun get(subscriptionsCallback: SubscriptionsCallback)
    fun getByUserId(userId: String, subscriptionsCallback: SubscriptionsCallback)
    fun deleteByBySubscriptionId(
        subscription: Subscription,
        deleteSubscriptionCallback: DeleteSubscriptionCallback
    )
}