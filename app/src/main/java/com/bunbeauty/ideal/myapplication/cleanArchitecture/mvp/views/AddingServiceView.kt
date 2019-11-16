package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views

import com.arellomobile.mvp.MvpView
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service

interface AddingServiceView: MvpView {
    fun showPremiumBlock(service: Service)
    fun hideMainBlocks()
   /* fun goToMyCalendar(status: String, serviceId: String)*/
    fun showAllDone()
    fun showMoreTenImages()
    fun showCategory()
    fun showNameInputError(error:String)
    fun showDescriptionInputError(error:String)
    fun showCostInputError(error:String)
    fun showCategoryInputError(error:String)
    fun showAddressInputError(error:String)
}