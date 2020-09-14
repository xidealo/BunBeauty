package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.log_in

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.log_in.iLogIn.IVerifyPhoneInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.VerifyPhonePresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.log_in.VerifyPhoneView

@InjectViewState
class VerifyPhonePresenter(private val verifyPhoneInteractor: IVerifyPhoneInteractor) :
    MvpPresenter<VerifyPhoneView>(), VerifyPhonePresenterCallback {

    fun getPhoneNumber(): String {
        return verifyPhoneInteractor.getPhoneNumber()
    }

    fun sendCode() {
        verifyPhoneInteractor.sendVerificationCode(verifyPhoneInteractor.getPhoneNumber(), this)
    }

    fun resendCode() {
        verifyPhoneInteractor.resendVerificationCode(verifyPhoneInteractor.getPhoneNumber())
    }

    override fun showTooManyRequestsError() {
        viewState.showMessage("Слишком много запросов. Попробуйте позже")
    }

    override fun showVerificationFailed() {
        viewState.showMessage("Ошибка. Что-то пошло не так")
    }

    fun checkCode(code: String) {
        viewState.showLoading()
        verifyPhoneInteractor.checkCode(code)
    }

    override fun showTooShortCodeError() {
        viewState.hideLoading()
        viewState.showMessage("Слишком короткий код")
    }

    override fun showWrongCodeError() {
        viewState.hideLoading()
        viewState.showMessage("Неправильный код")
    }

    override fun showServiceConnectionProblem() {
        viewState.hideLoading()
        viewState.showMessage("Проблемы соединения с сервером")
    }

    override fun goToRegistration(phone: String) {
        viewState.hideLoading()
        viewState.goToRegistration(phone)
    }

    override fun goToProfile() {
        viewState.goToProfile()
    }
}