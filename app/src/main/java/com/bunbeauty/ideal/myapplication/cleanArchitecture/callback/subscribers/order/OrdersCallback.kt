package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.order

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Order

interface OrdersCallback {
    fun returnOrders(orderList: List<Order>)
}