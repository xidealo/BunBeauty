package com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.elements.photoElement

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo

interface IChangeablePhotoElement {
    fun deletePhoto(photo: Photo)
    fun openPhoto(openedPhotoLinkOrUri: String)
}