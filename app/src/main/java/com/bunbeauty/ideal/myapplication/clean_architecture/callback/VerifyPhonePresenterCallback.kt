package com.bunbeauty.ideal.myapplication.clean_architecture.callback

interface VerifyPhonePresenterCallback {
    fun goToRegistration(phone: String)
    fun goToProfile()

    fun showTooManyRequestsError()
    fun showVerificationFailed()
    fun showTooShortCodeError()
    fun showWrongCodeError()
    fun showServiceConnectionProblem()
}
