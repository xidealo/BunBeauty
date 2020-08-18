package com.bunbeauty.ideal.myapplication.clean_architecture.business.log_in.iLogIn

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.logIn.AuthorizationPresenterCallback

interface IAuthorizationInteractor {
    fun defaultAuthorize(authorizationPresenterCallback: AuthorizationPresenterCallback)
    fun authorize(phone: String, authorizationPresenterCallback: AuthorizationPresenterCallback)
}