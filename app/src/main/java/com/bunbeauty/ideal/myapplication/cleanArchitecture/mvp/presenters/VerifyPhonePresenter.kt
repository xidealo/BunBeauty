package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.VerifyPhoneInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.VerifyPhoneView
import com.bunbeauty.ideal.myapplication.cleanArchitecture.ui.activities.logIn.VerifyPhoneActivity

@InjectViewState
class VerifyPhonePresenter(val verifyPhoneInteractor: VerifyPhoneInteractor): MvpPresenter<VerifyPhoneView>() {

    fun sendCode(verifyPhoneActivity: VerifyPhoneActivity){
        verifyPhoneInteractor.sendVerificationCode(verifyPhoneInteractor.getMyPhoneNumber(),  verifyPhoneActivity)
        viewState.showResendCode()
    }

    fun verify(code:String){
        if (code.trim().length >= 6) {
            verifyPhoneInteractor.verifyCode(verifyPhoneInteractor.getMyPhoneNumber(), code, viewState as VerifyPhoneActivity)
            viewState.showResendCode()
            viewState.hideViewsOnScreen()
        }else{
            viewState.showWrongCode()
        }

    }
}