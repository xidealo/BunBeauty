package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.logIn

interface AuthorizationPresenterCallback {
    fun showViewOnScreen()
    fun hideViewsOnScreen()
    fun setPhoneError()
    fun goToRegistration(phone: String)
    fun goToProfile()
    fun goToVerifyPhone(phone: String)
}