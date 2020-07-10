package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.photo.DeletePhotoCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.photo.PhotosCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.PhotoFirebase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.PhotoDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IPhotoRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class PhotoRepository(private val photoDao: PhotoDao, private val photoFirebase: PhotoFirebase) :
    IPhotoRepository, BaseRepository() {

    override fun insert(photo: Photo) {
        launch {
            photoFirebase.insert(photo)
        }
    }

    override fun delete(photo: Photo, deletePhotoCallback: DeletePhotoCallback) {
        launch {
            photoFirebase.delete(photo, deletePhotoCallback)
        }
    }

    override fun update(photo: Photo) {

    }

    override fun get(): List<Photo> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getByServiceId(
        serviceId: String, serviceOwnerId: String, photosCallback: PhotosCallback
    ) {
        photoFirebase.getByServiceId(serviceOwnerId, serviceId, photosCallback)
    }

    override fun getIdForNew(userId: String, serviceId: String): String =
        photoFirebase.getIdForNew(userId, serviceId)

}