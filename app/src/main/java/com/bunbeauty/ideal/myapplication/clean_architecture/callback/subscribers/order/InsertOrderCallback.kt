package com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.order

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.base_subscribers.BaseInsertCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Order

interface InsertOrderCallback : BaseInsertCallback<Order>


