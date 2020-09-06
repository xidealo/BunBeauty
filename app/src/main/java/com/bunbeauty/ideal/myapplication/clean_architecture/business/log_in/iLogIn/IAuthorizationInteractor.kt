package com.bunbeauty.ideal.myapplication.clean_architecture.business.log_in.iLogIn

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.logIn.AuthorizationPresenterCallback

interface IAuthorizationInteractor {
    fun authorizeByDefault(authorizationPresenterCallback: AuthorizationPresenterCallback)
    fun authorize(code: String, phone: String, authorizationPresenterCallback: AuthorizationPresenterCallback)
}