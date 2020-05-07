package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.subscriber.DeleteSubscriberCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.subscriber.InsertSubscriberCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.subscriber.SubscribersCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.subscriber.UpdateSubscriberCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.SubscriberFirebase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Subscriber
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.ISubscriberRepository
import kotlinx.coroutines.launch

class SubscriberRepository(private val subscriberFirebase: SubscriberFirebase) : BaseRepository(),
    ISubscriberRepository {

    private lateinit var subscribersCallback: SubscribersCallback

    override fun insert(
        subscriber: Subscriber,
        insertSubscriberCallback: InsertSubscriberCallback
    ) {
        launch {
            subscriber.id = subscriberFirebase.getIdForNew(subscriber.userId)
            subscriberFirebase.insert(subscriber)
            insertSubscriberCallback.returnCreatedCallback(subscriber)
        }
    }

    override fun delete(
        subscriber: Subscriber,
        deleteSubscriberCallback: DeleteSubscriberCallback
    ) {
        launch {

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
        this.subscribersCallback = subscribersCallback
        launch {

        }
    }

    override fun getByUserId(userId: String, subscribersCallback: SubscribersCallback) {
        this.subscribersCallback = subscribersCallback
        launch {

        }
    }


}