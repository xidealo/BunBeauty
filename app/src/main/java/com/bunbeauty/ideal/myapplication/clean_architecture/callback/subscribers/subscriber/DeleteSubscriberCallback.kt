package com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.subscriber

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.base_subscribers.BaseDeleteCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Subscriber

interface DeleteSubscriberCallback : BaseDeleteCallback<Subscriber>