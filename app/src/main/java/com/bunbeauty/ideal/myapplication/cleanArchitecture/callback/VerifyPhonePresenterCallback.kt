package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback

interface VerifyPhonePresenterCallback {
    fun hideViewsOnScreen()
    fun callbackWrongCode()
    fun showWrongCode()
    fun goToRegistration(phone: String)
    fun goToProfile()
    fun callbackGetUserName(name: String)
}
