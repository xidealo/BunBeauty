package com.bunbeauty.ideal.myapplication.clean_architecture.callback.service

import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service


interface EditServicePresenterCallback {
    fun showEditService(service: Service)
    fun addPhoto(photo: Photo)
    fun goToService(service: Service)
    fun goToProfile(service: Service)
    fun showNameInputError(error: String)
    fun showDescriptionInputError(error: String)
    fun showCostInputError(error: String)
    fun showCategoryInputError(error: String)
    fun showAddressInputError(error: String)
    fun showDurationInputError(error: String)
    fun saveTags(service: Service)
}