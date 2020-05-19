package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback

interface VerifyPhonePresenterCallback {
    fun goToRegistration(phone: String)
    fun goToProfile()

    fun showTooManyRequestsError()
    fun showVerificationFailed()
    fun showTooShortCodeError()
    fun showWrongCodeError()
}
