package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.photo.DeletePhotoCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.photo.PhotosCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.PhotoServiceFirebase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.PhotoDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IPhotoServiceRepository
import kotlinx.coroutines.launch

class PhotoServiceRepository(private val photoDao: PhotoDao, private val photoServiceFirebase: PhotoServiceFirebase) :
    IPhotoServiceRepository, BaseRepository() {

    override fun insert(photo: Photo) {
        launch {
            photoServiceFirebase.insert(photo)
        }
    }

    override fun delete(photo: Photo, deletePhotoCallback: DeletePhotoCallback) {
        launch {
            photoServiceFirebase.delete(photo, deletePhotoCallback)
        }
    }

    override fun update(photo: Photo) {}

    override fun get(): List<Photo> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getByServiceId(
        serviceId: String, serviceOwnerId: String, photosCallback: PhotosCallback
    ) {
        photoServiceFirebase.getByServiceId(serviceOwnerId, serviceId, photosCallback)
    }

    override fun getIdForNew(userId: String, serviceId: String): String =
        photoServiceFirebase.getIdForNew(userId, serviceId)

}