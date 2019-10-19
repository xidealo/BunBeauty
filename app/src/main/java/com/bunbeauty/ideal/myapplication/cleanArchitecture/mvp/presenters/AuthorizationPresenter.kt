package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.AuthorizationInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.AuthorizationView

@InjectViewState
class AuthorizationPresenter(private val authorizationInteractor: AuthorizationInteractor):
        MvpPresenter<AuthorizationView>() {
    fun authorize(){
        //business logic class
        if (authorizationInteractor.getCurrentFbUser() != null) {
            viewState.hideViewsOnScreen()
            if (authorizationInteractor.getUserName() != null) {
                viewState.goToProfile()
            } else {
                viewState.goToRegistration()
            }
            //val myAuth = MyAuthorization(this@AuthorizationActivity, myPhoneNumber)
            //myAuth.authorizeUser()
        } else {
            viewState.showViewsOnScreen()
        }
    }

    fun authorize(myPhoneNumber:String){
        authorizationInteractor.clearUsers()
        if (authorizationInteractor.isPhoneCorrect(myPhoneNumber.trim { it <= ' ' })) {
            viewState.enableVerifyBtn(false)
            viewState.goToVerifyPhone(myPhoneNumber)
        } else {
            viewState.setPhoneError()
        }
    }
}