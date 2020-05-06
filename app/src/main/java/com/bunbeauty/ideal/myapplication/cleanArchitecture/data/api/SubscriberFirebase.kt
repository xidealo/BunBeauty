package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.subscriber.SubscribersCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Subscriber
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SubscriberFirebase {

    fun insert(subscriber: Subscriber) {
        val subscriberRef = FirebaseDatabase.getInstance()
            .getReference(User.USERS)
            .child(subscriber.userId)
            .child(Subscriber.SUBSCRIBERS)
            .child(subscriber.id)

        val items = HashMap<String, Any>()
        items[Subscriber.SUBSCRIBER_ID] = subscriber.subscriberId
        items[Subscriber.DATE] = subscriber.date
        subscriberRef.updateChildren(items)
    }

    fun getByUserId(userId: String, subscribersCallback: SubscribersCallback) {

        val servicesRef = FirebaseDatabase.getInstance()
            .getReference(User.USERS)
            .child(userId)
            .child(Subscriber.SUBSCRIBERS)

        servicesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(subscribersSnapshot: DataSnapshot) {
                val subscribers = arrayListOf<Subscriber>()
                for (SubscriberSnapshot in subscribersSnapshot.children) {
                    subscribers.add(getSubscriberFromSnapshot(subscribersSnapshot, userId))
                }
                subscribersCallback.returnList(subscribers)
            }

            override fun onCancelled(error: DatabaseError) {
                // Some error
            }
        })

    }

    private fun getSubscriberFromSnapshot(
        subscriptionSnapshot: DataSnapshot,
        userId: String
    ): Subscriber {

        val subscriber = Subscriber()
        subscriber.id = subscriptionSnapshot.key!!
        subscriber.date = subscriptionSnapshot.child(Subscriber.DATE).value as? String ?: ""
        subscriber.subscriberId =
            subscriptionSnapshot.child(Subscriber.SUBSCRIBER_ID).value as? String ?: ""
        subscriber.userId = userId
        return subscriber
    }

    fun getIdForNew(userId: String) = FirebaseDatabase.getInstance().getReference(User.USERS)
        .child(userId)
        .child(Subscriber.SUBSCRIBERS).push().key!!
}