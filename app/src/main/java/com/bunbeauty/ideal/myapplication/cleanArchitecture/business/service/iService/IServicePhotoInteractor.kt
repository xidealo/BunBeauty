package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service.iService

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.service.ServicePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service

interface IServicePhotoInteractor {
    fun getServicePhotos(service: Service, servicePresenterCallback: ServicePresenterCallback)
    fun getPhotosLink(): ArrayList<Photo>
}