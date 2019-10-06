package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views

import com.arellomobile.mvp.MvpView

//@StateStrategyType(value = )
interface AuthorizationView : MvpView {
    fun hideViewsOnScreen()
    fun showViewsOnScreen()
    fun setPhoneError()
    fun enableVerifyBtn(status:Boolean)
    fun goToVerifyPhone(phone:String)
    fun hideKeyboard()
}