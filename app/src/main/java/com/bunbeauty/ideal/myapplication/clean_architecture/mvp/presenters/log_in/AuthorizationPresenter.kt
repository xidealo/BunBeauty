package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.log_in

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.business.log_in.iLogIn.IAuthorizationInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.logIn.AuthorizationPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.log_in.AuthorizationView

@InjectViewState
class AuthorizationPresenter(private val authorizationInteractor: IAuthorizationInteractor) :
        MvpPresenter<AuthorizationView>(), AuthorizationPresenterCallback {

    fun defaultAuthorize() {
        viewState.hideViewsOnScreen()
        authorizationInteractor.defaultAuthorize(this)
    }

    fun authorize(phone: String) {
        authorizationInteractor.authorize(phone, this)
    }

    override fun showViewOnScreen() {
        viewState.showViewsOnScreen()
    }

    override fun setPhoneError() {
        viewState.enableButton()
        viewState.showPhoneError("Некорректный номер телефона")
    }

    override fun goToRegistration(phone: String) {
        viewState.goToRegistration(phone)
    }

    override fun goToProfile(user: User) {
        viewState.goToProfile(user)
    }

    override fun goToVerifyPhone(phone: String) {
        viewState.goToVerifyPhone(phone)
    }
}