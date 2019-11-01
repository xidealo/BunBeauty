package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback

import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User

interface ServiceCallback {
    fun callbackGetService(service: Service)
    //fun callbackGetServiceList(serviceList: List<Service>)
}