package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service

interface ServiceCallback {
    fun callbackGetService(service: Service)
    //fun callbackGetServiceList(serviceList: List<Service>)
}