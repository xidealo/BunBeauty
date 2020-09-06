package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.log_in

import com.arellomobile.mvp.MvpView
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User

//@StateStrategyType(value = )
interface AuthorizationView : MvpView {
    fun hidePhoneNumberFields()
    fun showPhoneNumberFields()
    fun hideLoading()
    fun showLoading()
    fun showPhoneError(error:String)
    fun goToVerifyPhone(phone:String)
    fun goToRegistration(phone: String)
    fun goToProfile(user: User)
}