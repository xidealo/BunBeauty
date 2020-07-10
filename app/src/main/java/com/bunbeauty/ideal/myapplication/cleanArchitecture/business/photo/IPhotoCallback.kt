package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.photo

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo

interface IPhotoCallback {
    fun returnPhotos(photos: List<Photo>)
}