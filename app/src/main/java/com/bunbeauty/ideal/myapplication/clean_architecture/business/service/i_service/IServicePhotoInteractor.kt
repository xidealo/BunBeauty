package com.bunbeauty.ideal.myapplication.clean_architecture.business.service.i_service

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.service.ServicePresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service

interface IServicePhotoInteractor {
    fun getServicePhotos(service: Service, servicePresenterCallback: ServicePresenterCallback)
    fun getPhotoLinkList(): List<Photo>
}