package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views

import com.arellomobile.mvp.MvpView
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service

interface CreationServiceView : MvpView {
    fun showPremiumBlock(service: Service)
    fun hideMainBlock()
    fun showMainBlock()
    fun showMessage(message: String)
    fun updatePhotoFeed(photoLinkList: List<Photo>)
    fun showNameInputError(error: String)
    fun showDescriptionInputError(error: String)
    fun showCostInputError(error: String)
    fun showCategorySpinnerError(error: String)
    fun showAddressInputError(error: String)
}