package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.AuthorizationInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.IUserSubscriber
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.AuthorizationView

@InjectViewState
class AuthorizationPresenter(private val authorizationInteractor: AuthorizationInteractor):
        MvpPresenter<AuthorizationView>(), IUserSubscriber {

    fun authorize(){
        if (authorizationInteractor.getCurrentFbUser() != null) {
            viewState.hideViewsOnScreen()
            authorizationInteractor.getUserName(this)
        } else {
            viewState.showViewsOnScreen()
        }
    }

    fun authorize(myPhoneNumber:String){
        if (authorizationInteractor.isPhoneCorrect(myPhoneNumber.trim { it <= ' ' })) {
            viewState.goToVerifyPhone(myPhoneNumber)
        } else {
            viewState.setPhoneError()
        }
    }

    override fun returnUser(user: User) {
        if (user.name.isEmpty()) {
            viewState.goToRegistration()
        } else {
            viewState.goToProfile()
        }
    }
}