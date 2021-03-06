package com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.subscription

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.base_subscribers.BaseGetListCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Subscription

interface SubscriptionsCallback : BaseGetListCallback<Subscription>