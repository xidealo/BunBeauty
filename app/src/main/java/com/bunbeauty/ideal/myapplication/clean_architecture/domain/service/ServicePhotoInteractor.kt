package com.bunbeauty.ideal.myapplication.clean_architecture.domain.service

import com.bunbeauty.ideal.myapplication.clean_architecture.domain.service.i_service.IServicePhotoInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.service.ServicePresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.photo.PhotosCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.PhotoServiceRepository

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
        if (service.photos.isNotEmpty()) {
            servicePresenterCallback.showPhotos(service.photos)
            photoList.clear()
            photoList.addAll(service.photos)
            return
        } else
            photoServiceRepository.getByServiceId(service.id, service.userId, this)
    }

    override fun returnList(photos: List<Photo>) {
        photoList.clear()
        photoList.addAll(photos)
        servicePresenterCallback.showPhotos(photos)
    }
}