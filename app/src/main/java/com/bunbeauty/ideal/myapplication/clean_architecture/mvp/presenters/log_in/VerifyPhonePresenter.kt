package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.log_in

import android.content.Context
import android.content.Intent
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.log_in.iLogIn.IVerifyPhoneInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.VerifyPhonePresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.log_in.VerifyPhoneView

@InjectViewState
class VerifyPhonePresenter(
    private val verifyPhoneInteractor: IVerifyPhoneInteractor,
    private val intent: Intent,
    private val context: Context,
) :
    MvpPresenter<VerifyPhoneView>(), VerifyPhonePresenterCallback {

    fun getPhoneNumber(): String {
        return verifyPhoneInteractor.getPhoneNumber(intent)
    }

    fun sendCode() {
        verifyPhoneInteractor.sendVerificationCode(
            verifyPhoneInteractor.getPhoneNumber(intent),
            this
        )
    }

    fun resendCode() {
        verifyPhoneInteractor.resendVerificationCode(verifyPhoneInteractor.getPhoneNumber(intent))
    }

    override fun showTooManyRequestsError() {
        viewState.showMessage(context.resources.getString(R.string.too_many_requests_error))
    }

    override fun showVerificationFailed() {
        viewState.showMessage(context.resources.getString(R.string.verification_failed_error))
    }

    fun checkCode(code: String) {
        viewState.showLoading()
        verifyPhoneInteractor.checkCode(code)
    }

    override fun showTooShortCodeError() {
        viewState.hideLoading()
        viewState.showMessage(context.resources.getString(R.string.too_short_code_error))
    }

    override fun showWrongCodeError() {
        viewState.hideLoading()
        viewState.showMessage(context.resources.getString(R.string.wrong_code_error))
    }

    override fun showServiceConnectionProblem() {
        viewState.hideLoading()
        viewState.showMessage(context.resources.getString(R.string.server_connection_error))
    }

    override fun goToRegistration(phone: String) {
        viewState.hideLoading()
        viewState.goToRegistration(phone)
    }

    override fun goToProfile() {
        viewState.goToProfile()
    }
}