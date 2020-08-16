package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service.iService.IServicePhotoInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.service.ServicePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.photo.PhotosCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.PhotoServiceRepository

class ServicePhotoInteractor(private val photoServiceRepository: PhotoServiceRepository) :
    IServicePhotoInteractor, PhotosCallback {

    private lateinit var servicePresenterCallback: ServicePresenterCallback
    private var photoList: MutableList<Photo> = ArrayList()

    override fun getPhotoLinkList() = photoList

    override fun getServicePhotos(
        service: Service,
        servicePresenterCallback: ServicePresenterCallback
    ) {
        this.servicePresenterCallback = servicePresenterCallback
        photoServiceRepository.getByServiceId(service.id, service.userId, this)
    }

    override fun returnList(photos: List<Photo>) {
        photoList.clear()
        photoList.addAll(photos)
        servicePresenterCallback.showPhotos(photos)
    }
}