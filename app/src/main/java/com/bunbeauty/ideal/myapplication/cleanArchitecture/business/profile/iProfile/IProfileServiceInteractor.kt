package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.iProfile

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.profile.ProfilePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service

interface IProfileServiceInteractor {
    fun getServicesByUserId(
        userId: String,
        profilePresenterCallback: ProfilePresenterCallback
    )
    fun getServices(): MutableList<Service>
}