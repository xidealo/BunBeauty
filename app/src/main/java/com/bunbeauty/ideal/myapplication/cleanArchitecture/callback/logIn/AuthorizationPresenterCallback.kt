package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.logIn

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface AuthorizationPresenterCallback {
    fun showViewOnScreen()
    fun setPhoneError()
    fun goToRegistration(phone: String)
    fun goToProfile(user: User)
    fun goToVerifyPhone(phone: String)
}