package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.photo.PhotosCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.PhotoFirebase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.PhotoDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IPhotoRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class PhotoRepository(private val photoDao: PhotoDao, private val photoFirebase: PhotoFirebase): IPhotoRepository,
        BaseRepository(){

    override fun insert(photo: Photo) {
        launch {
            photo.id = getIdForNew(photo.userId, photo.serviceId)
            photoFirebase.insert(photo)
            //photoDao.insert(photo)
        }
    }

    override fun delete(photo: Photo) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun update(photo: Photo) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun get(): List<Photo> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun getByServiceId(serviceId: String, serviceOwnerId: String, photosCallback: PhotosCallback,
                       isFirstEnter: Boolean) {
        val photoList: ArrayList<Photo> = ArrayList()

        if (isFirstEnter) {
            photoFirebase.getByServiceId(serviceOwnerId, serviceId, photosCallback)
        } else {
            runBlocking {
                photoList.addAll(photoDao.findAllByServiceId(serviceId))
            }
            photosCallback.returnList(photoList)
        }
    }

    fun getIdForNew(userId: String, serviceId:String): String = photoFirebase.getIdForNew(userId,serviceId)

}