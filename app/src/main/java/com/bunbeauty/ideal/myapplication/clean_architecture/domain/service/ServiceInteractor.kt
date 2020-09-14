package com.bunbeauty.ideal.myapplication.clean_architecture.domain.service

import android.content.Intent
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.service.i_service.IServiceInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.service.ServicePresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.service.GetServiceCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Order
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.IServiceRepository

class ServiceInteractor(
    private val serviceRepository: IServiceRepository,
    private val intent: Intent
) : IServiceInteractor, GetServiceCallback {

    override lateinit var gottenService: Service
    private lateinit var servicePresenterCallback: ServicePresenterCallback

    override fun getService(servicePresenterCallback: ServicePresenterCallback) {
        this.servicePresenterCallback = servicePresenterCallback

        if (intent.hasExtra(Service.SERVICE)) {
            returnGottenObject(intent.getParcelableExtra(Service.SERVICE) as? Service)
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
        servicePresenterCallback.checkMaster(service.userId)
    }

    override fun updateService(
        service: Service,
        servicePresenterCallback: ServicePresenterCallback
    ) {
        gottenService = service
        servicePresenterCallback.showService(service)
        servicePresenterCallback.setTitle(service.name)
        servicePresenterCallback.getServicePhotos(service)
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