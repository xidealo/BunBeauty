package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.iLogIn

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.logIn.AuthorizationPresenterCallback

interface IAuthorizationInteractor {
    fun defaultAuthorize(authorizationPresenterCallback: AuthorizationPresenterCallback)
    fun authorize(phone: String, authorizationPresenterCallback: AuthorizationPresenterCallback)
}