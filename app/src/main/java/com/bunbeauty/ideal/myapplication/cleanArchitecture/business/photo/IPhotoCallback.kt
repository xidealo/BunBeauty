package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.photo

import android.net.Uri
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo

interface IPhotoCallback {
    fun returnPhotos(photos: List<Photo>)
    fun returnCreatedPhotoLink(uri: Uri)
}