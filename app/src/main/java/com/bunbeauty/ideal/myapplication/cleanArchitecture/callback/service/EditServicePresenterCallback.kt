package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.service

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Tag
import java.util.ArrayList


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