package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views

import com.arellomobile.mvp.MvpView

interface AddingServiceView: MvpView {
    fun showPremiumState ()
    fun setWithPremium()
    fun goToMyCalendar(status: String, serviceId: String)
    fun showAllDone()
    fun showWrongCode()
    fun showOldCode()
    fun showPremiumActivated()
    fun showMoreTenImages()


    fun showNameInputError(error:String)
    fun showDescriptionInputError(error:String)
    fun showCostInputError(error:String)
    fun showCategoryInputError(error:String)
    fun showAddressInputError(error:String)
}