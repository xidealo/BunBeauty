package com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.subscriber

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.base_subscribers.BaseGetListCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Subscriber

interface SubscribersCallback : BaseGetListCallback<Subscriber>