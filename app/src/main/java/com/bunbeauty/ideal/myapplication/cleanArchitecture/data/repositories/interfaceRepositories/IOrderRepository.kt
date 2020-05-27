package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.order.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Order

interface IOrderRepository {
    fun insert(order: Order, insertOrderCallback: InsertOrderCallback)
    fun delete(order: Order, deleteOrderCallback: DeleteOrderCallback)
    fun update(order: Order, updateOrderCallback: UpdateOrderCallback)
    fun get(ordersCallback: OrdersCallback)

    fun getById(userId: String, orderId: String, orderCallback: OrderCallback)
}