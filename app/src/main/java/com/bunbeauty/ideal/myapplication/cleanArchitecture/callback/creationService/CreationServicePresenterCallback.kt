package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.creationService

interface CreationServicePresenterCallback {
    fun showNameInputError(error: String)
    fun showDescriptionInputError(error: String)
    fun showCostInputError(error: String)
    fun showCategoryInputError(error: String)
    fun showAddressInputError(error: String)
}