package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.photo.DeletePhotoCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.photo.PhotosCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo

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