package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.AuthorizationInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.AuthorizationCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.AuthorizationView

@InjectViewState
class AuthorizationPresenter(private val authorizationInteractor: AuthorizationInteractor):
        MvpPresenter<AuthorizationView>(), AuthorizationCallback {

    fun authorize(){
        if (authorizationInteractor.getCurrentFbUser() != null) {
            viewState.hideViewsOnScreen()
            authorizationInteractor.getUserName(this)
        } else {
            viewState.showViewsOnScreen()
        }
    }

    fun authorize(phone:String){
        if (authorizationInteractor.isPhoneCorrect(phone.trim())) {
            viewState.goToVerifyPhone(phone)
        } else {
            viewState.setPhoneError()
        }
    }

    override fun goToRegistration(phone: String) {
        viewState.goToRegistration(phone)
    }

    override fun goToProfile() {
        viewState.goToProfile()
    }
}