package com.bunbeauty.ideal.myapplication.clean_architecture.callback.service

import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service


interface EditServicePresenterCallback {
    fun showEditService(service:Service)
    fun addPhoto(photo: Photo)
    fun goToService(service: Service)
    fun goToProfile(service: Service)
    fun nameEditServiceInputError()
    fun nameEditServiceInputErrorEmpty()
    fun nameEditServiceInputErrorLong()
    fun saveTags(service: Service)
}