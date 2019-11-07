package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback

interface AuthorizationCallback {
    fun goToRegistration(phone: String)
    fun goToProfile()
}