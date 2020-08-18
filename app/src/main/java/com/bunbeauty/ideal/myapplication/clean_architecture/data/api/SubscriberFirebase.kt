package com.bunbeauty.ideal.myapplication.clean_architecture.data.api

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.subscriber.SubscribersCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Subscriber
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.google.firebase.database.*

class SubscriberFirebase {

    fun insert(subscriber: Subscriber) {
        val subscriberRef = FirebaseDatabase.getInstance()
            .getReference(Subscriber.SUBSCRIBERS)
            .child(subscriber.userId)
            .child(subscriber.id)

        val items = HashMap<String, Any>()
        items[Subscriber.SUBSCRIBER_ID] = subscriber.subscriberId
        items[Subscriber.DATE] = ServerValue.TIMESTAMP
        subscriberRef.updateChildren(items)
    }

    fun delete(subscriber: Subscriber) {
        val subscriberRef = FirebaseDatabase.getInstance()
            .getReference(Subscriber.SUBSCRIBERS)
            .child(subscriber.userId)
            .child(subscriber.id)

        subscriberRef.removeValue()
    }

    fun getByUserId(userId: String, subscribersCallback: SubscribersCallback) {

        val servicesRef = FirebaseDatabase.getInstance()
            .getReference(Subscriber.SUBSCRIBERS)
            .child(userId)

        servicesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(subscribersSnapshot: DataSnapshot) {
                val subscribers = arrayListOf<Subscriber>()
                for (subscriberSnapshot in subscribersSnapshot.children) {
                    subscribers.add(getSubscriberFromSnapshot(subscriberSnapshot, userId))
                }
                subscribersCallback.returnList(subscribers)
            }

            override fun onCancelled(error: DatabaseError) {
                // Some error
            }
        })
    }

    fun getBySubscriberId(
        subscriberId: String,
        ownerId: String,
        subscribersCallback: SubscribersCallback
    ) {
        val subscribersQuery = FirebaseDatabase.getInstance()
            .getReference(Subscriber.SUBSCRIBERS)
            .child(ownerId)
            .orderByChild(Subscriber.SUBSCRIBER_ID)
            .equalTo(subscriberId)

        subscribersQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(subscribersSnapshot: DataSnapshot) {
                val subscribers = mutableListOf<Subscriber>()
                if (subscribersSnapshot.childrenCount > 0L) {
                    subscribers.add(
                        getSubscriberFromSnapshot(
                            subscribersSnapshot.children.iterator().next(),
                            ownerId
                        )
                    )
                }
                subscribersCallback.returnList(subscribers)
            }

            override fun onCancelled(databaseError: DatabaseError) {
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
        subscriber.date = subscriptionSnapshot.child(Subscriber.DATE).value as? Long ?: 0
        subscriber.subscriberId =
            subscriptionSnapshot.child(Subscriber.SUBSCRIBER_ID).value as? String ?: ""
        subscriber.userId = userId
        return subscriber
    }

    fun getIdForNew(userId: String) = FirebaseDatabase.getInstance().getReference(User.USERS)
        .child(userId)
        .child(Subscriber.SUBSCRIBERS).push().key!!
}