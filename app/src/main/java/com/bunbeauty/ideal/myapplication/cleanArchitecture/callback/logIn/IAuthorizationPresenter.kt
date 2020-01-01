package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.logIn

interface IAuthorizationPresenter {
    fun showViewOnScreen()
    fun setPhoneError()
    fun goToRegistration(phone: String)
    fun goToProfile()
    fun goToVerifyPhone(phone: String)
}