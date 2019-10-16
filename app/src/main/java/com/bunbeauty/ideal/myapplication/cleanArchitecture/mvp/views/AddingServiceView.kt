package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views

import com.arellomobile.mvp.MvpView

interface AddingServiceView: MvpView {
    fun showPremium ()
    fun setPremium ()
    fun setWithPremium()
    fun goToMyCalendar(status: String, serviceId: String)
    fun showAllDone()
    fun showWrongCode()
    fun showOldCode()
    fun showPremiumActivated()
    fun showMoreTenImages()
}