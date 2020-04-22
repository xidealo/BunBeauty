package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback

interface CheckPremiumPresenterCallback {
    fun showError(error:String)
    fun showPremiumActivated()
    fun activatePremium()
}