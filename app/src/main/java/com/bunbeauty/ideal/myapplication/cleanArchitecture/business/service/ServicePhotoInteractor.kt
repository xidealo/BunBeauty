package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service.iService.IServicePhotoInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.service.ServicePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.photo.PhotosCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.PhotoRepository

class ServicePhotoInteractor(private val photoRepository: PhotoRepository) :
    IServicePhotoInteractor,
    PhotosCallback {

    private lateinit var servicePresenterCallback: ServicePresenterCallback

    override fun getServicePhotos(
        service: Service,
        servicePresenterCallback: ServicePresenterCallback
    ) {
        this.servicePresenterCallback = servicePresenterCallback
        photoRepository.getByServiceId(service.id, service.userId, this)
    }

    override fun returnList(objects: List<Photo>) {
        servicePresenterCallback.showPhotos(objects)
    }
}