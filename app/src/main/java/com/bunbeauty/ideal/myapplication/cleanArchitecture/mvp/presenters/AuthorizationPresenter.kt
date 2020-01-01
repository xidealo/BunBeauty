package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.AuthorizationInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.logIn.IAuthorizationPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.AuthorizationView

@InjectViewState
class AuthorizationPresenter(private val authorizationInteractor: AuthorizationInteractor) :
        MvpPresenter<AuthorizationView>(), IAuthorizationPresenter {

    fun authorize() {
        viewState.hideViewsOnScreen()
        authorizationInteractor.authorize(this)
    }

    fun authorize(phone: String) {
        viewState.disableButton()
        authorizationInteractor.authorize(phone, this)
    }

    override fun showViewOnScreen() {
        viewState.showViewsOnScreen()
    }

    override fun setPhoneError() {
        viewState.enableButton()
        viewState.showPhoneError("Неккоректный номер телефона")
    }

    override fun goToRegistration(phone: String) {
        viewState.goToRegistration(phone)
    }

    override fun goToProfile() {
        viewState.goToProfile()
    }

    override fun goToVerifyPhone(phone: String) {
        viewState.goToVerifyPhone(phone)
    }
}