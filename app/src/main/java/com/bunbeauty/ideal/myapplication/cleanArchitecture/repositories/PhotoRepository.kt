package com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.IPhotoCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.PhotoFirebase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.PhotoDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.interfaceRepositories.IPhotoRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class PhotoRepository(private val photoDao: PhotoDao, private val photoFirebase: PhotoFirebase): IPhotoRepository,
        BaseRepository(), IPhotoCallback{

    lateinit var photoCallback: IPhotoCallback

    override fun insert(photo: Photo) {
        launch {
            photoDao.insert(photo)
        }
        photoFirebase.insert(photo)
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

    fun getIdForNew(userId: String, serviceId:String): String = photoFirebase.getIdForNew(userId,serviceId)

    fun getByServiceId(serviceId: String, serviceOwnerId: String, photoCallback: IPhotoCallback,
                       isFirstEnter: Boolean) {
        this.photoCallback = photoCallback
        val photoList: ArrayList<Photo> = ArrayList()

        if (isFirstEnter) {
            photoFirebase.getByServiceId(serviceOwnerId, serviceId, this)
        } else {
            runBlocking {
                photoList.addAll(photoDao.findAllByServiceId(serviceId))
            }
            photoCallback.returnPhotos(photoList)
        }
    }

    override fun returnPhotos(photos: List<Photo>) {
        photoCallback.returnPhotos(photos)
    }

}