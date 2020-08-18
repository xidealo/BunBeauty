package com.bunbeauty.ideal.myapplication.clean_architecture.adapters.elements.photoElement

import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Photo

interface IChangeablePhotoElement {
    fun deletePhoto(photo: Photo)
    fun openPhoto(openedPhotoLinkOrUri: String)
}