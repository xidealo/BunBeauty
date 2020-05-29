package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.iProfile.IProfileServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.profile.ProfilePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.service.ServicesCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IServiceRepository

class ProfileServiceInteractor(private val serviceRepository: IServiceRepository) :
    IProfileServiceInteractor, ServicesCallback {

    private var services = mutableListOf<Service>()
    private lateinit var profilePresenterCallback: ProfilePresenterCallback

    override fun getServices() = services

    override fun getServicesByUserId(
        userId: String,
        profilePresenterCallback: ProfilePresenterCallback
    ) {
        this.profilePresenterCallback = profilePresenterCallback
        serviceRepository.getServicesByUserId(
            userId,
            this,
            true
        )
    }

    override fun returnServices(serviceList: List<Service>) {
        services.addAll(serviceList)
        profilePresenterCallback.setServiceList(serviceList)
    }

}