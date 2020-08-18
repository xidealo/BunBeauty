package com.bunbeauty.ideal.myapplication.clean_architecture.callback

import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service

interface IServiceRepositoryCallback {
    fun callbackServicesFound(services: List<Service>)
}