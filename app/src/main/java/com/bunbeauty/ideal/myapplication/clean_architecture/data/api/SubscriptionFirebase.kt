package com.bunbeauty.ideal.myapplication.clean_architecture.data.api

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.subscription.SubscriptionsCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Subscription
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.google.firebase.database.*

class SubscriptionFirebase {

    fun insert(subscription: Subscription) {
        val subscriptionRef = FirebaseDatabase.getInstance()
            .getReference(Subscription.SUBSCRIPTIONS)
            .child(subscription.userId)
            .child(subscription.id)

        val items = HashMap<String, Any>()
        items[Subscription.SUBSCRIPTION_ID] = subscription.subscriptionId
        items[Subscription.DATE] = ServerValue.TIMESTAMP
        subscriptionRef.updateChildren(items)
    }

    fun delete(subscription: Subscription) {
        val subscriptionRef = FirebaseDatabase.getInstance()
            .getReference(Subscription.SUBSCRIPTIONS)
            .child(subscription.userId)
            .child(subscription.id)

        subscriptionRef.removeValue()
    }



    fun getByUserId(
        userId: String,
        subscriptionsCallback: SubscriptionsCallback
    ) {
        val subscriptionRef = FirebaseDatabase.getInstance()
            .getReference(Subscription.SUBSCRIPTIONS)
            .child(userId)

        subscriptionRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(subscriptionsSnapshot: DataSnapshot) {
                val subscriptions = arrayListOf<Subscription>()
                for (subscriptionSnapshot in subscriptionsSnapshot.children) {
                    subscriptions.add(getSubscriptionFromSnapshot(subscriptionSnapshot, userId))
                }
                subscriptionsCallback.returnList(subscriptions)
            }

            override fun onCancelled(error: DatabaseError) {
                // Some error
            }
        })
    }

    fun getByUserId(
        userId: String,
        loadingLimit: Int,
        subscriptionsCallback: SubscriptionsCallback
    ) {
        val subscriptionRef = FirebaseDatabase.getInstance()
            .getReference(Subscription.SUBSCRIPTIONS)
            .child(userId)
            .limitToLast(loadingLimit)

        subscriptionRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(subscriptionsSnapshot: DataSnapshot) {
                val subscriptions = arrayListOf<Subscription>()
                for (subscriptionSnapshot in subscriptionsSnapshot.children) {
                    subscriptions.add(getSubscriptionFromSnapshot(subscriptionSnapshot, userId))
                }
                subscriptionsCallback.returnList(subscriptions)
            }

            override fun onCancelled(error: DatabaseError) {
                // Some error
            }
        })
    }

    fun deleteByBySubscriptionId(
        subscription: Subscription
    ) {
        val subscriptionsQuery = FirebaseDatabase.getInstance()
            .getReference(Subscription.SUBSCRIPTIONS)
            .child(subscription.userId)
            .orderByChild(Subscription.SUBSCRIPTION_ID)
            .equalTo(subscription.subscriptionId)

        subscriptionsQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(subscriptionsSnapshot: DataSnapshot) {
                if (subscriptionsSnapshot.childrenCount > 0L) {
                    delete(
                        getSubscriptionFromSnapshot(
                            subscriptionsSnapshot.children.iterator().next(),
                            subscription.userId
                        )
                    )
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Some error
            }
        })

    }

    private fun getSubscriptionFromSnapshot(
        subscriptionSnapshot: DataSnapshot,
        userId: String
    ): Subscription {

        val subscription = Subscription()
        subscription.id = subscriptionSnapshot.key!!
        subscription.date = subscriptionSnapshot.child(Subscription.DATE).value as? Long ?: 0
        subscription.subscriptionId =
            subscriptionSnapshot.child(Subscription.SUBSCRIPTION_ID).value as? String ?: ""
        subscription.userId = userId
        return subscription
    }

    fun getIdForNew(userId: String) = FirebaseDatabase.getInstance().getReference(User.USERS)
        .child(userId)
        .child(Subscription.SUBSCRIPTIONS).push().key!!

}