package com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.order.DeleteOrderCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.order.InsertOrderCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.order.OrderCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.order.OrdersCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.api.OrderFirebase
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Order
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.IOrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrderRepository(private val orderFirebase: OrderFirebase) : BaseRepository(),
    IOrderRepository {

    override fun insert(order: Order, insertOrderCallback: InsertOrderCallback) {
        launch {
            val insertedOrder = orderFirebase.insert(order)
            withContext(Dispatchers.Main) {
                insertOrderCallback.returnCreatedCallback(insertedOrder)
            }
        }
    }

    override fun delete(order: Order, deleteOrderCallback: DeleteOrderCallback) {
        launch {
            orderFirebase.delete(order)
            withContext(Dispatchers.Main) {
                deleteOrderCallback.returnDeletedCallback(order)
            }
        }
    }

    override fun getByUserId(userId: String, ordersCallback: OrdersCallback) {
        launch {
            orderFirebase.getByUserId(userId, ordersCallback)
        }
    }

    override fun getById(userId: String, orderId: String, orderCallback: OrderCallback) {
        launch {
            orderFirebase.getById(userId, orderId, orderCallback)
        }
    }
}