package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service.iService.IServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.service.ServicePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.service.GetServiceCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.service.GetServicesCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Order
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IServiceRepository

class ServiceInteractor(
    private val serviceRepository: IServiceRepository,
    private val intent: Intent
) : IServiceInteractor, GetServiceCallback {

    override lateinit var gottenService: Service
    private lateinit var servicePresenterCallback: ServicePresenterCallback

    override fun getService(servicePresenterCallback: ServicePresenterCallback) {
        this.servicePresenterCallback = servicePresenterCallback

        if (intent.hasExtra(Service.SERVICE)) {
            returnGottenObject(intent.getSerializableExtra(Service.SERVICE) as Service)
        } else {
            val masterId = intent.getSerializableExtra(Order.MASTER_ID) as String
            val serviceId = intent.getSerializableExtra(Order.SERVICE_ID) as String
            serviceRepository.getById(serviceId, masterId, true, this)
        }
    }

    override fun returnGottenObject(service: Service?) {
        gottenService = service!!

        servicePresenterCallback.showService(service)
        servicePresenterCallback.getServicePhotos(service)
        servicePresenterCallback.getUser(service.userId)
    }

    override fun updateService(
        service: Service,
        servicePresenterCallback: ServicePresenterCallback
    ) {
        gottenService = service
        servicePresenterCallback.showService(service)
        servicePresenterCallback.setTitle(service.name)
    }


    override fun iconClick(user: User, servicePresenterCallback: ServicePresenterCallback) {
        if (isMyService(user)) {
            servicePresenterCallback.goToEditService(gottenService)
        } else {
            servicePresenterCallback.goToProfile(user)
        }
    }

    private fun isMyService(user: User): Boolean {
        return User.getMyId() == user.id
    }
}