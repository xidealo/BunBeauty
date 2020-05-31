package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.logIn

import com.arellomobile.mvp.MvpView
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

//@StateStrategyType(value = )
interface AuthorizationView : MvpView {
    fun hideViewsOnScreen()
    fun showViewsOnScreen()
    fun showPhoneError(error:String)
    fun enableVerifyBtn(status:Boolean)
    fun goToVerifyPhone(phone:String)
    fun goToRegistration(phone: String)
    fun goToProfile(user: User)
    fun hideKeyboard()
    fun disableButton()
    fun enableButton()
}