package com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.order

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.base_subscribers.BaseGetCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Order

interface OrderCallback : BaseGetCallback<Order>