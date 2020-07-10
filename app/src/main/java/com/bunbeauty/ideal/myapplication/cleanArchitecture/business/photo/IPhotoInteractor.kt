package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.photo

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service

interface IPhotoInteractor {
    fun addPhoto(photo: Photo)
    fun addPhotos(photos: List<Photo>)
    fun removePhoto(photo: Photo)
    fun getPhotosLink(): List<Photo>
    fun getDeletePhotosLink(): List<Photo>
    fun saveImages(service: Service)
    fun deleteImages()
    fun getPhotos(service: Service, iPhotoCallback: IPhotoCallback)
}