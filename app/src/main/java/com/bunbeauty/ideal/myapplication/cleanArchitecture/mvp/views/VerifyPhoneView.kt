package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views

import com.arellomobile.mvp.MvpView

interface VerifyPhoneView: MvpView {
    fun hideViewsOnScreen()
    fun showViewsOnScreen()
    fun showSendCode()
    fun showWrongCode()
    fun callbackWrongCode()
    fun goToRegistration()
    fun goToProfile()
}