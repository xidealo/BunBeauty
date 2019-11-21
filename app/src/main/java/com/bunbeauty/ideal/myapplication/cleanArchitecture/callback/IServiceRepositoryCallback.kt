package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service

interface IServiceRepositoryCallback {
    fun callbackServicesFound(services: List<Service>)
}