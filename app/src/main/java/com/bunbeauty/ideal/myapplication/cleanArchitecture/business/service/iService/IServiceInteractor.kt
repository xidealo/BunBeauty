package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service.iService

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.service.ServicePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface IServiceInteractor {
    fun createServiceScreen(servicePresenterCallback: ServicePresenterCallback)
    fun iconClick(servicePresenterCallback: ServicePresenterCallback)
}