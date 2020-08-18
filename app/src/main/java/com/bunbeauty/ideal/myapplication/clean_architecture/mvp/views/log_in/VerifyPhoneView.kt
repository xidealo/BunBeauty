package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.log_in

import com.arellomobile.mvp.MvpView

interface VerifyPhoneView : MvpView {
    fun hideViewsOnScreen()
    fun showViewsOnScreen()
    fun showMessage(message: String)
    fun goToRegistration(phone: String)
    fun goToProfile()
}