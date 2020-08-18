package com.bunbeauty.ideal.myapplication.clean_architecture.business.photo

import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User

interface IPhotoInteractor {
    fun addPhoto(photo: Photo)
    fun addPhotos(photos: List<Photo>)
    fun removePhoto(photo: Photo)
    fun getPhotosLink(): List<Photo>
    fun getDeletePhotosLink(): List<Photo>
    fun savePhotos(photos: List<Photo>, service: Service, iPhotoCallback: IPhotoCallback)
    fun savePhotos(photos: List<Photo>, user: User, iPhotoCallback: IPhotoCallback)
    fun deleteImagesFromService(photos: List<Photo>)
    fun deletePhotosFromStorage(location: String, photos: List<Photo>)
    fun getPhotos(service: Service, iPhotoCallback: IPhotoCallback)
}