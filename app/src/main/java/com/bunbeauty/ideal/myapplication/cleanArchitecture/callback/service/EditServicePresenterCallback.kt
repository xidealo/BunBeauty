package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.service

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service


interface EditServicePresenterCallback {
    fun showEditService(service:Service)
    fun goToService(service: Service)
    fun goToProfile(service: Service)
    fun nameEditServiceInputError()
    fun nameEditServiceInputErrorEmpty()
    fun nameEditServiceInputErrorLong()
}