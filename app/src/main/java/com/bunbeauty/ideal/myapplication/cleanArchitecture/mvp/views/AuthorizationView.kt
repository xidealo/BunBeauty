package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views

import com.arellomobile.mvp.MvpView

interface AuthorizationView : MvpView {
    fun hideViewsOfScreen()
    fun showViewsOnScreen()
    fun setPhoneError()
    fun enableVerifyBtn()

}