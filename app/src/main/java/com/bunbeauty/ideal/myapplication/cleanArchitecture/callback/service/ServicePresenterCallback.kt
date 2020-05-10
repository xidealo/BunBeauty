package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.service

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface ServicePresenterCallback {
    fun showMyService(service: Service)
    fun showPremium(service: Service)
    fun createOwnServiceTopPanel(service: Service)
    fun createAlienServiceTopPanel(user: User, service: Service)

    fun goToEditService(service: Service)
    fun goToProfile(user: User)

}