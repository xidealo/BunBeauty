package com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.PhotoFirebase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.PhotoDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Photo
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.interfaceRepositories.IPhotoRepository
import kotlinx.coroutines.launch

class PhotoRepository(val photoDao: PhotoDao, val photoFirebase: PhotoFirebase): IPhotoRepository, BaseRepository(){
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

}