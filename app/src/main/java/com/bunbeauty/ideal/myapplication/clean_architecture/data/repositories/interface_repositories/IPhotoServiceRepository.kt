package com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.photo.DeletePhotoCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.photo.PhotosCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Photo

interface IPhotoServiceRepository {
    fun insert(photo: Photo)
    fun delete(photo: Photo, deletePhotoCallback: DeletePhotoCallback)
    fun update(photo: Photo)
    fun get(): List<Photo>
    fun getByServiceId(
        serviceId: String, serviceOwnerId: String, photosCallback: PhotosCallback
    )

    fun getIdForNew(userId: String, serviceId: String): String
}