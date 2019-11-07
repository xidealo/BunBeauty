package com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.interfaceRepositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo

interface IPhotoRepository {
    fun insert(photo: Photo)
    fun delete(photo: Photo)
    fun update(photo: Photo)
    fun get(): List<Photo>

}