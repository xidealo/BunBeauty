package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.iLogIn.IVerifyPhoneInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.VerifyPhonePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.logIn.VerifyPhoneActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.VerifyPhoneView
import com.google.firebase.auth.PhoneAuthProvider

@InjectViewState
class VerifyPhonePresenter(private val verifyPhoneInteractor: IVerifyPhoneInteractor) :
        MvpPresenter<VerifyPhoneView>(), VerifyPhonePresenterCallback {

    //рефактор, проверки в интерактор
    private val TAG = "DBInf"

    fun sendCode() {
        verifyPhoneInteractor.sendVerificationCode(
                verifyPhoneInteractor.getMyPhoneNumber(),
                this)
    }

    fun resendCode(verifyPhoneActivity: VerifyPhoneActivity) {
        /*verifyPhoneInteractor.resendVerificationCode(verifyPhoneInteractor.getMyPhoneNumber(),
                resendToken!!,
                verifyPhoneActivity)
        viewState.showSendCode()*/
    }

    fun verify(code: String) {
        verifyPhoneInteractor.verify(code, this)
    }

    override fun hideViewsOnScreen() {
        viewState.hideViewsOnScreen()
    }

    override fun callbackWrongCode() {
        viewState.callbackWrongCode()
    }

    override fun showWrongCode() {
        viewState.showWrongCode()
    }

    override fun goToRegistration(phone: String) {
        viewState.goToRegistration(phone)
    }

    override fun goToProfile() {
        viewState.goToProfile()
    }

    override fun showSendCode() {
        viewState.showSendCode()
    }

    override fun sendVerificationCode(phoneNumber: String, callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks) {
        viewState.sendVerificationCode(phoneNumber, callback)
    }
}