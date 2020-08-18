package com.bunbeauty.ideal.myapplication.clean_architecture.business.service.i_service

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.service.ServicePresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User

interface IServiceInteractor {

    var gottenService: Service

    fun getService(servicePresenterCallback: ServicePresenterCallback)
    fun updateService(service: Service,  servicePresenterCallback: ServicePresenterCallback)
    fun iconClick(user: User, servicePresenterCallback: ServicePresenterCallback)
}