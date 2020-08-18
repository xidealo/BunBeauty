package com.bunbeauty.ideal.myapplication.clean_architecture.business.photo

import android.net.Uri
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Photo

interface IPhotoCallback {
    fun returnPhotos(photos: List<Photo>)
    fun returnCreatedPhotoLink(uri: Uri)
}