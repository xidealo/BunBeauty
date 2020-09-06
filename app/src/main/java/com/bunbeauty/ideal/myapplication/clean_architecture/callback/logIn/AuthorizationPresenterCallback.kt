package com.bunbeauty.ideal.myapplication.clean_architecture.callback.logIn

import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User

interface AuthorizationPresenterCallback {
    fun showDefaultAuthorizationFailed()
    fun setPhoneError()
    fun goToRegistration(phone: String)
    fun goToProfile(user: User)
    fun goToVerifyPhone(phone: String)
}