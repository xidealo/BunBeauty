package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service

interface ServiceRepositoryCallback {
    fun callbackServicesFound(services: List<Service>)
}