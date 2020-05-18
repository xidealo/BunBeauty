package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.logIn

import com.arellomobile.mvp.MvpView
import com.google.firebase.auth.PhoneAuthProvider

interface VerifyPhoneView : MvpView {
    fun hideViewsOnScreen()
    fun showViewsOnScreen()
    fun showSendCode()

    fun showMessage(message: String)

    fun goToRegistration(phone: String)
    fun goToProfile()
}