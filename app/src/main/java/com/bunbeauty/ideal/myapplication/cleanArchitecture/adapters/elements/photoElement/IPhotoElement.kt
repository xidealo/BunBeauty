package com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.elements.photoElement

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo

interface IPhotoElement {
    fun deletePhoto(photo: Photo)
    fun openPhoto(openedPhotoLinkOrUri: String)
}