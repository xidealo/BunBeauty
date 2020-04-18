package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback

import com.google.firebase.auth.PhoneAuthProvider

interface VerifyPhonePresenterCallback {
    fun hideViewsOnScreen()
    fun callbackWrongCode()
    fun showWrongCode()
    fun goToRegistration(phone: String)
    fun goToProfile()
    fun showSendCode()

    fun sendVerificationCode(
            phoneNumber: String,
            callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks)

    fun resendVerificationCode(
            phoneNumber: String,
            callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks,
            token: PhoneAuthProvider.ForceResendingToken)
}
