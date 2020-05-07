package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.subscriber.DeleteSubscriberCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.subscriber.InsertSubscriberCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.subscriber.SubscribersCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.subscriber.UpdateSubscriberCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Subscriber

interface ISubscriberRepository {
    fun insert(subscriber: Subscriber, insertSubscriberCallback: InsertSubscriberCallback)
    fun delete(subscriber: Subscriber, deleteSubscriberCallback: DeleteSubscriberCallback)
    fun update(subscriber: Subscriber, updateSubscriberCallback: UpdateSubscriberCallback)
    fun get(subscribersCallback: SubscribersCallback)
    fun getByUserId(userId: String, subscribersCallback: SubscribersCallback)
    fun getBySubscriberId(subscriberId: String, ownerId: String, subscribersCallback: SubscribersCallback)
}