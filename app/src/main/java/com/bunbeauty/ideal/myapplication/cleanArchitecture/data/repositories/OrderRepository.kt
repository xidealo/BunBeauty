package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.orders.IOrderCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.order.DeleteOrderCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.order.InsertOrderCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.order.OrdersCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.order.UpdateOrderCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.OrderFirebase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.OrderDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Order
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IOrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrderRepository(
    private val orderFirebase: OrderFirebase,
    private val orderDao: OrderDao
) : BaseRepository(), IOrderRepository, IOrderCallback {

    override fun insert(order: Order, insertOrderCallback: InsertOrderCallback) {
        launch {
            order.id = orderFirebase.getIdForNew(order.clientId)
            orderFirebase.insert(order)
            //orderDao.insert(order)
            withContext(Dispatchers.Main) {
                insertOrderCallback.returnCreatedCallback(order)
            }
        }
    }

    override fun delete(order: Order, deleteOrderCallback: DeleteOrderCallback) {
        launch {
            //orderDao.delete(order)
            orderFirebase.delete(order)
            withContext(Dispatchers.Main) {
                deleteOrderCallback.returnDeletedCallback(order)
            }
        }

    }

    override fun update(order: Order, updateOrderCallback: UpdateOrderCallback) {
        launch {
            //orderDao.update(order)
            orderFirebase.update(order)
            withContext(Dispatchers.Main) {
                updateOrderCallback.returnUpdatedCallback(order)
            }
        }
    }

    override fun get(ordersCallback: OrdersCallback) {
        launch {
            val orders = orderDao.get()
            withContext(Dispatchers.Main) {
                ordersCallback.returnOrders(orders)
            }
        }
    }


}