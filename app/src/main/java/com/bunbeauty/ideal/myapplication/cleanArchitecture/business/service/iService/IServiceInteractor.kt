package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service.iService

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.service.ServicePresenterCallback

interface IServiceInteractor {
    fun createServiceScreen(servicePresenterCallback: ServicePresenterCallback)
}