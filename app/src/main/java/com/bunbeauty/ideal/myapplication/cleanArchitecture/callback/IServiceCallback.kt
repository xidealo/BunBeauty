package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service

interface IServiceCallback {
    fun returnServiceList(serviceList: List<Service>)
    fun returnService(service: Service)
}