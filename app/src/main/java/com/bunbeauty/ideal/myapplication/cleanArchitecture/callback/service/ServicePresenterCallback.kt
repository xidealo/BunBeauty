package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.service

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface ServicePresenterCallback {
    fun showService(user: User, service: Service)
    fun showPremium(service: Service)
}