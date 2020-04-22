package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.creationService

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service

interface CreationServicePresenterCallback {
    fun showNameInputError(error: String)
    fun showDescriptionInputError(error: String)
    fun showCostInputError(error: String)
    fun showCategoryInputError(error: String)
    fun showAddressInputError(error: String)
    fun showServiceCreated(service: Service)
}