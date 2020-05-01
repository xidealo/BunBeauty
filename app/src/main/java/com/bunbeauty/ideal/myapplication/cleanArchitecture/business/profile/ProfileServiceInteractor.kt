package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.iProfile.IProfileServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.profile.ProfilePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.service.IServicesCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.ServiceRepository

class ProfileServiceInteractor(private val serviceRepository: ServiceRepository) :
    IProfileServiceInteractor, IServicesCallback {

    private var services = mutableListOf<Service>()
    private lateinit var profilePresenterCallback: ProfilePresenterCallback

    override fun getServicesLink() = services

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