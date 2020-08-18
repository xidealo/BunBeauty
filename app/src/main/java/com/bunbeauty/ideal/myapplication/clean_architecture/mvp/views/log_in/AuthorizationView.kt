package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.log_in

import com.arellomobile.mvp.MvpView
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User

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