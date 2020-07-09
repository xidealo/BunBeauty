package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.createService.iCreateService

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service

interface ICreationServicePhotoInteractor {
    fun addPhoto(photo: Photo)
    fun removePhoto(photo: Photo)
    fun returnPhotos(): List<Photo>
    fun addImages(service: Service)
}