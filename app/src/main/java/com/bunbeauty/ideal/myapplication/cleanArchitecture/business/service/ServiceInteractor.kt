package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service.iService.IServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.service.ServicePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

class ServiceInteractor(private val intent: Intent) : IServiceInteractor {

    private lateinit var gottenService: Service

    override fun getService(): Service {
        return intent.getSerializableExtra(Service.SERVICE) as Service
    }

    override fun createServiceScreen(servicePresenterCallback: ServicePresenterCallback) {
        val service = getService()
        gottenService = service

        servicePresenterCallback.showService(service)
        servicePresenterCallback.getServicePhotos(service)
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