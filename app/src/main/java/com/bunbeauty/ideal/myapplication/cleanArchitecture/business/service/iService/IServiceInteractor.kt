package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service.iService

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.service.ServicePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface IServiceInteractor {
    fun updateService(service: Service,  servicePresenterCallback: ServicePresenterCallback)
    fun createServiceScreen(servicePresenterCallback: ServicePresenterCallback)
    fun iconClick(servicePresenterCallback: ServicePresenterCallback)
}