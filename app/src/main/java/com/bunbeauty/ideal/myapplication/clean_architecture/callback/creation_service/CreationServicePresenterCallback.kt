package com.bunbeauty.ideal.myapplication.clean_architecture.callback.creation_service

import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service

interface CreationServicePresenterCallback {
    fun showNameInputError(error: String)
    fun showDescriptionInputError(error: String)
    fun showCostInputError(error: String)
    fun showCategoryInputError(error: String)
    fun showAddressInputError(error: String)
    fun showDurationInputError(error: String)
    fun showServiceCreated(service: Service)

    fun addTags(service: Service)
    fun addPhotos(service: Service)
}