package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.log_in

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.logIn.AuthorizationPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.api.StringApi
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.log_in.iLogIn.IAuthorizationInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.log_in.AuthorizationView

@InjectViewState
class AuthorizationPresenter(
    private val authorizationInteractor: IAuthorizationInteractor,
    private val stringApi: StringApi
) : MvpPresenter<AuthorizationView>(), AuthorizationPresenterCallback {

    fun authorizeByDefault() {
        viewState.hidePhoneNumberFields()
        viewState.showLoading()
        authorizationInteractor.authorizeByDefault(this)
    }

    override fun showDefaultAuthorizationFailed() {
        viewState.showPhoneNumberFields()
        viewState.hideLoading()
    }

    fun authorize(code: String, phone: String) {
        viewState.showLoading()
        authorizationInteractor.authorize(code, stringApi.getPhoneNumberDigits(phone), this)
    }

    override fun setPhoneError() {
        viewState.hideLoading()
        viewState.showPhoneError("Некорректный номер телефона")
    }

    override fun goToRegistration(phone: String) {
        viewState.goToRegistration(phone)
    }

    override fun goToProfile(user: User) {
        viewState.goToProfile(user)
    }

    override fun goToVerifyPhone(phone: String) {
        viewState.hideLoading()
        viewState.goToVerifyPhone(phone)
    }
}