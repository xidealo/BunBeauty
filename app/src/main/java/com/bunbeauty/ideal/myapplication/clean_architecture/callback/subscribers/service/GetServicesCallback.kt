package com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.service

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.base_subscribers.BaseGetListCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service

interface GetServicesCallback: BaseGetListCallback<Service> {
}