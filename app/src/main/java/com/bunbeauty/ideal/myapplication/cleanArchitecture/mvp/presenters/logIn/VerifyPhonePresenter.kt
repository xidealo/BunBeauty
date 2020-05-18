package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.logIn

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.iLogIn.IVerifyPhoneInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.VerifyPhonePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.logIn.VerifyPhoneView

@InjectViewState
class VerifyPhonePresenter(private val verifyPhoneInteractor: IVerifyPhoneInteractor) :
    MvpPresenter<VerifyPhoneView>(), VerifyPhonePresenterCallback {

    private val TAG = "DBInf"

    fun sendCode() {
        verifyPhoneInteractor.sendVerificationCode(verifyPhoneInteractor.getMyPhoneNumber(), this)
    }

    fun resendCode() {
        verifyPhoneInteractor.resendVerificationCode(verifyPhoneInteractor.getMyPhoneNumber())
    }

    override fun showTooManyRequestsError() {
        viewState.showMessage("Слишком много запросов. Попробуйте позже")
    }

    override fun showVerificationFailed() {
        viewState.showMessage("Ошибка. Что-то пошло не так")
    }

    fun checkCode(code: String) {
        verifyPhoneInteractor.checkCode(code)
    }

    override fun showTooShortCodeError() {
        viewState.showMessage("Слишком короткий код")
    }

    override fun showWrongCodeError() {
        viewState.showMessage("Неправильный код")
    }

    override fun hideViewsOnScreen() {
        viewState.hideViewsOnScreen()
    }

    override fun goToRegistration(phone: String) {
        viewState.goToRegistration(phone)
    }

    override fun goToProfile() {
        viewState.goToProfile()
    }
}