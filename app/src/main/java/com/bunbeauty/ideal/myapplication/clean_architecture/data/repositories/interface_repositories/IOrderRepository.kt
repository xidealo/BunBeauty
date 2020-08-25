package com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.order.*
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Order

interface IOrderRepository {

    fun insert(order: Order, insertOrderCallback: InsertOrderCallback)
    fun delete(order: Order, deleteOrderCallback: DeleteOrderCallback)
    fun getByUserId(userId: String, ordersCallback: OrdersCallback)
    fun getById(userId: String, orderId: String, orderCallback: OrderCallback)

    /*
     fun update(order: Order, updateOrderCallback: UpdateOrderCallback)
     fun get(ordersCallback: OrdersCallback)

     fun getById(userId: String, orderId: String, orderCallback: OrderCallback)*/
}