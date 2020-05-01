package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.logIn

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.iLogIn.IVerifyPhoneInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.VerifyPhonePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.logIn.VerifyPhoneView
import com.google.firebase.auth.PhoneAuthProvider

@InjectViewState
class VerifyPhonePresenter(private val verifyPhoneInteractor: IVerifyPhoneInteractor) :
        MvpPresenter<VerifyPhoneView>(), VerifyPhonePresenterCallback {

    private val TAG = "DBInf"

    fun sendCode() {
        verifyPhoneInteractor.sendVerificationCode(
                verifyPhoneInteractor.getMyPhoneNumber(),
                this
        )
    }

    fun resendCode() {
        verifyPhoneInteractor.resendVerificationCode(verifyPhoneInteractor.getMyPhoneNumber(), this)
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

    override fun sendVerificationCode(
            phoneNumber: String,
            callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        viewState.sendVerificationCode(phoneNumber, callback)
    }

    override fun resendVerificationCode(
            phoneNumber: String,
            callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks,
            token: PhoneAuthProvider.ForceResendingToken
    ) {
        viewState.resendVerificationCode(phoneNumber, callback, token)
    }
}