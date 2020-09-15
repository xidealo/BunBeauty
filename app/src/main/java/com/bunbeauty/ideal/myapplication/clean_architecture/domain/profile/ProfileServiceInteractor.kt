package com.bunbeauty.ideal.myapplication.clean_architecture.domain.profile

import com.bunbeauty.ideal.myapplication.clean_architecture.domain.profile.iProfile.IProfileServiceInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.profile.ProfilePresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.service.GetServicesCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.IServiceRepository

class ProfileServiceInteractor(private val serviceRepository: IServiceRepository) :
    IProfileServiceInteractor, GetServicesCallback {

    private lateinit var profilePresenterCallback: ProfilePresenterCallback

    override fun getServicesByUserId(
        userId: String?,
        profilePresenterCallback: ProfilePresenterCallback
    ) {
        if (userId == null) {
            return
        }

        this.profilePresenterCallback = profilePresenterCallback
        serviceRepository.getByUserId(userId, this, true)
    }

    override fun returnList(objects: List<Service>) {
        profilePresenterCallback.showServiceList(objects)
    }

}