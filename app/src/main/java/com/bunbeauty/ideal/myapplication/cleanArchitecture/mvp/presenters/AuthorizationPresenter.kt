package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.AuthorizationInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import javax.inject.Inject

//@InjectViewState
class AuthorizationPresenter/*: MvpPresenter<AuthorizationView>() */{

    @Inject
    lateinit var authorizationInteractor:AuthorizationInteractor

    private lateinit var appComponent: AppComponent

    init {
        appComponent = DaggerAppComponent.builder().build()
        appComponent.inject(this)
    }

    fun authorize(){

        //business logic class
        if (authorizationInteractor.getCurrentFbUser() != null) {
            //viewState.hideViewsOnScreen()
            val myPhoneNumber = authorizationInteractor.getCurrentFbUser()!!.phoneNumber
            //val myAuth = MyAuthorization(this@AuthorizationActivity, myPhoneNumber)
            //myAuth.authorizeUser()
        } else {
            //viewState.showViewsOnScreen()
        }
    }

    fun authorize(myPhoneNumber:String){
        if (authorizationInteractor.isPhoneCorrect(myPhoneNumber.trim { it <= ' ' })) {
            //viewState.enableVerifyBtn(false)
            //viewState.goToVerifyPhone(myPhoneNumber)
        } else {
            //viewState.setPhoneError()
        }
    }
}