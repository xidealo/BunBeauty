package com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.subscriber.DeleteSubscriberCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.subscriber.InsertSubscriberCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.subscriber.SubscribersCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.subscriber.UpdateSubscriberCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.api.SubscriberFirebase
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Subscriber
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.ISubscriberRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SubscriberRepository(private val subscriberFirebase: SubscriberFirebase) : BaseRepository(),
    ISubscriberRepository {

    override fun insert(
        subscriber: Subscriber,
        insertSubscriberCallback: InsertSubscriberCallback
    ) {
        launch {
            subscriber.id = subscriberFirebase.getIdForNew(subscriber.userId)
            subscriberFirebase.insert(subscriber)
            withContext(Dispatchers.Main){
                insertSubscriberCallback.returnCreatedCallback(subscriber)
            }
        }
    }

    override fun delete(
        subscriber: Subscriber,
        deleteSubscriberCallback: DeleteSubscriberCallback
    ) {
        launch {
            subscriberFirebase.delete(subscriber)
            withContext(Dispatchers.Main){
                deleteSubscriberCallback.returnDeletedCallback(subscriber)
            }
        }
    }

    override fun update(
        subscriber: Subscriber,
        updateSubscriberCallback: UpdateSubscriberCallback
    ) {
        launch {

        }
    }

    override fun get(subscribersCallback: SubscribersCallback) {
        launch {

        }
    }

    override fun getByUserId(userId: String, subscribersCallback: SubscribersCallback) {
        launch {
            subscriberFirebase.getByUserId(userId, subscribersCallback)
        }
    }

    override fun getBySubscriberId(
        subscriberId: String,
        ownerId: String,
        subscribersCallback: SubscribersCallback
    ) {
        launch {
            subscriberFirebase.getBySubscriberId(subscriberId, ownerId, subscribersCallback)
        }
    }

}