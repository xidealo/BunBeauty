package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service.iService.IServiceServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.service.ServicePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

class ServiceServiceInteractor(
    private val intent: Intent
) : IServiceServiceInteractor {

    private lateinit var service: Service

    override fun createServiceScreen(
        user: User,
        servicePresenterCallback: ServicePresenterCallback
    ) {
        val service = intent.getSerializableExtra(Service.SERVICE) as Service

        this.service = service

        if (!isMyService(user)) {
            servicePresenterCallback.showPremium(service)
            servicePresenterCallback.createAlienServiceTopPanel(user, service)
        } else {
            servicePresenterCallback.createOwnServiceTopPanel(service)
        }

        servicePresenterCallback.showService(service)
        servicePresenterCallback.getServicePhotos(service)
    }

    override fun iconClick(user: User, servicePresenterCallback: ServicePresenterCallback) {
        if (isMyService(user)) {
            servicePresenterCallback.goToEditService(service)
        } else {
            servicePresenterCallback.goToProfile(user)
        }
    }

    private fun isMyService(user: User): Boolean = User.getMyId() == user.id

    override fun updateService(
        service: Service,
        servicePresenterCallback: ServicePresenterCallback
    ) {
        this.service = service
        servicePresenterCallback.showService(service)
        servicePresenterCallback.setTitle(service.name)
    }

}