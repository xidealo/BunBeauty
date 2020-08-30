package com.bunbeauty.ideal.myapplication.clean_architecture.business.service.i_service

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.service.ServicePresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User

interface IServiceUserInteractor {
    fun checkMaster(userId: String, servicePresenterCallback: ServicePresenterCallback)
    fun getUser(): User
}