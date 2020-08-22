package com.bunbeauty.ideal.myapplication.clean_architecture.business.log_in

import android.content.Intent
import com.bunbeauty.ideal.myapplication.clean_architecture.business.api.VerifyPhoneNumberApi
import com.bunbeauty.ideal.myapplication.clean_architecture.business.log_in.iLogIn.IVerifyPhoneInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.VerifyPhoneNumberCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.VerifyPhonePresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.user.UserCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.BaseRepository
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.IUserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential

class VerifyPhoneInteractor(
    private val userRepository: IUserRepository,
    private val intent: Intent,
    private val verifyPhoneNumberApi: VerifyPhoneNumberApi
) : BaseRepository(), IVerifyPhoneInteractor, UserCallback, VerifyPhoneNumberCallback {

    private lateinit var verifyPresenterCallback: VerifyPhonePresenterCallback

    override fun getPhoneNumber(): String = intent.getStringExtra(User.PHONE)!!

    override fun sendVerificationCode(
        phoneNumber: String,
        verifyPresenterCallback: VerifyPhonePresenterCallback
    ) {
        this.verifyPresenterCallback = verifyPresenterCallback

        verifyPhoneNumberApi.sendVerificationCode(phoneNumber, this)
    }

    override fun resendVerificationCode(phoneNumber: String) {
        verifyPhoneNumberApi.resendVerificationCode(phoneNumber)
    }

    override fun checkCode(code: String) {
        verifyPhoneNumberApi.checkCode(code, this)
    }

    override fun returnTooManyRequestsError() {
        verifyPresenterCallback.showTooManyRequestsError()
    }

    override fun returnVerificationFailed() {
        verifyPresenterCallback.showVerificationFailed()
    }

    override fun returnTooShortCodeError() {
        verifyPresenterCallback.showTooShortCodeError()
    }

    override fun returnCredential(credential: PhoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                userRepository.getByPhoneNumber(getPhoneNumber(), this, true)
            } else {
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    verifyPresenterCallback.showWrongCodeError()
                }
            }
        }
    }

    override fun returnServiceConnectionProblem() {
        verifyPresenterCallback.showServiceConnectionProblem()
    }

    override fun returnGottenObject(element: User?) {
        if (element == null) return

        if (element.name.isEmpty()) {
            verifyPresenterCallback.goToRegistration(getPhoneNumber())
        } else {
            verifyPresenterCallback.goToProfile()
        }
    }

    /*override fun returnWrongCodeError() {
        verifyPresenterCallback.showWrongCodeError()
    }

    override fun returnVerifySuccessful(credential: PhoneAuthCredential) {
        userRepository.getByPhoneNumber(getMyPhoneNumber(), this, true)
    }*/


    companion object {
        const val TAG = "DBInf"
    }



}