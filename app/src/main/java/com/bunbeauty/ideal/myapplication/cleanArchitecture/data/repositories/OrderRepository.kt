package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.order.InsertOrderCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.order.OrdersCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.OrderFirebase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Order
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IOrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrderRepository(private val orderFirebase: OrderFirebase) : BaseRepository(),
    IOrderRepository {

    override fun insert(order: Order, insertOrderCallback: InsertOrderCallback) {
        launch {
            val insertedOrder = orderFirebase.insert(order)
            //orderDao.insert(order)
            withContext(Dispatchers.Main) {
                insertOrderCallback.returnCreatedCallback(insertedOrder)
            }
        }
    }

    override fun getByUserId(userId: String, ordersCallback: OrdersCallback) {
        launch {
            orderFirebase.getByUserId(userId, ordersCallback)
        }
    }

}