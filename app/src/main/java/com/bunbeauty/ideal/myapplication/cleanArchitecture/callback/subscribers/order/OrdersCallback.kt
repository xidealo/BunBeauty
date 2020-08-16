package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.order

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.baseSubscribers.BaseGetListCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Order

interface OrdersCallback: BaseGetListCallback<Order> {
}