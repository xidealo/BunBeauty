package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.logIn

import com.arellomobile.mvp.MvpView

interface VerifyPhoneView : MvpView {
    fun showSendCode()
    fun hideViewsOnScreen()
    fun showViewsOnScreen()
    fun showMessage(message: String)
    fun goToRegistration(phone: String)
    fun goToProfile()
}