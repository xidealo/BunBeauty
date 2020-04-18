package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views

import com.arellomobile.mvp.MvpView
import com.google.firebase.auth.PhoneAuthProvider

interface VerifyPhoneView : MvpView {
    fun hideViewsOnScreen()
    fun showViewsOnScreen()
    fun showSendCode()
    fun showWrongCode()
    fun callbackWrongCode()
    fun goToRegistration(phone: String)
    fun goToProfile()
    fun sendVerificationCode(phoneNumber: String, callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks)
}