package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.order.DeleteOrderCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.order.InsertOrderCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.order.OrdersCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.order.UpdateOrderCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Order

interface IOrderRepository {
    fun insert(order: Order, insertOrderCallback: InsertOrderCallback)
    fun delete(order: Order, deleteOrderCallback: DeleteOrderCallback)
    fun update(order: Order, updateOrderCallback: UpdateOrderCallback)
    fun get(ordersCallback: OrdersCallback)
}