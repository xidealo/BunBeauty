package com.bunbeauty.ideal.myapplication.clean_architecture.data.api

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.order.OrderCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.order.OrdersCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Order
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule.Session
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

    fun delete(order: Order) {
        val orderRef = FirebaseDatabase.getInstance()
            .getReference(Order.ORDERS)
            .child(order.clientId)
            .child(order.id)

        orderRef.removeValue()
    }

    fun getByUserId(userId: String, ordersCallback: OrdersCallback) {
        val ordersReference = FirebaseDatabase.getInstance()
            .getReference(Order.ORDERS)
            .child(userId)

        ordersReference.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(ordersSnapshot: DataSnapshot) {
                val orderList = ordersSnapshot.children.map {
                    getOrderFromSnapshot(it, userId)
                }
                ordersCallback.returnList(orderList)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun getById(userId: String, orderId: String, orderCallback: OrderCallback) {
        val ordersReference = FirebaseDatabase.getInstance()
            .getReference(Order.ORDERS)
            .child(userId)
            .child(orderId)

        ordersReference.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(ordersSnapshot: DataSnapshot) {
                if (ordersSnapshot.hasChildren()) {
                    val order = getOrderFromSnapshot(ordersSnapshot, userId)
                    orderCallback.returnGottenObject(order)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun getOrderFromSnapshot(orderSnapshot: DataSnapshot, userId: String): Order {
        return Order(
            id = orderSnapshot.key!!,
            clientId = userId,
            masterId = orderSnapshot.child(Order.MASTER_ID).value as String,
            serviceId = orderSnapshot.child(Order.SERVICE_ID).value as String,
            serviceName = orderSnapshot.child(Order.SERVICE_NAME).value as String,
            session = Session(
                orderSnapshot.child(Session.START_TIME).value as Long,
                orderSnapshot.child(Session.FINISH_TIME).value as Long
            )
        )
    }

    fun getIdForNew(clientId: String) = FirebaseDatabase.getInstance()
        .getReference(Order.ORDERS)
        .child(clientId)
        .push()
        .key!!


    fun update(order: Order) {}
    fun get(order: Order) {}
}