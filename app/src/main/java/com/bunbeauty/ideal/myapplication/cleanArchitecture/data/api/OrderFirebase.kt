package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.order.OrderCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Order
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.Session
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OrderFirebase {

    fun insert(order: Order): Order {
        order.id = getIdForNew(order.clientId)
        val orderReference = FirebaseDatabase.getInstance()
            .getReference(Order.ORDERS)
            .child(order.clientId)
            .child(order.id)

        val items = HashMap<String, Any>()
        items[Order.MASTER_ID] = order.masterId
        items[Order.SERVICE_ID] = order.serviceId
        items[Order.SERVICE_NAME] = order.serviceName
        items[Session.START_TIME] = order.session.startTime
        items[Session.FINISH_TIME] = order.session.finishTime

        orderReference.updateChildren(items)

        return order
    }

    fun delete(order: Order) {}
    fun update(order: Order) {}
    fun get(order: Order) {}

    fun getOrderById(userId: String, orderId: String, orderCallback: OrderCallback) {

        val orderRef = FirebaseDatabase.getInstance()
            .getReference(User.USERS)
            .child(userId)
            .child(Order.ORDERS)
            .child(orderId)

        orderRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(ordersSnapshot: DataSnapshot) {
                orderCallback.returnElement(
                    getOrderFromSnapshot(
                        ordersSnapshot.children.iterator().next(), userId
                    )
                )
            }

            override fun onCancelled(error: DatabaseError) {
                // Some error
            }
        })

    }

    private fun getOrderFromSnapshot(orderSnapshot: DataSnapshot, userId: String): Order {
        /*val order = Order(session = Session(0L, 0L))
        order.id = orderSnapshot.key!!
        order.masterId = orderSnapshot.child(Order.MASTER_ID).value as? String ?: ""
        order.serviceId = orderSnapshot.child(Order.SERVICE_ID).value as? String ?: ""
        //order.time = orderSnapshot.child(Order.TIME).value as? Long ?: 0L*/
        //order.userId = userId

        return Order("", "", "", "", "", Session(0L, 0L))
    }

    fun getIdForNew(clientId: String): String {
        return FirebaseDatabase.getInstance()
            .getReference(Order.ORDERS)
            .child(clientId)
            .push()
            .key!!
    }
}