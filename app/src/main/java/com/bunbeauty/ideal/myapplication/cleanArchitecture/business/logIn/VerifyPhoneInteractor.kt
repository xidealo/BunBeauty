package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.api.VerifyPhoneNumberApi
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.iLogIn.IVerifyPhoneInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.VerifyPhoneNumberCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.VerifyPhonePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.user.UsersCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.BaseRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IUserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential

class VerifyPhoneInteractor(
    private val userRepository: IUserRepository,
    private val intent: Intent,
    private val verifyPhoneNumberApi: VerifyPhoneNumberApi
) : BaseRepository(), IVerifyPhoneInteractor, UsersCallback, VerifyPhoneNumberCallback {

    private lateinit var verifyPresenterCallback: VerifyPhonePresenterCallback

    override fun getMyPhoneNumber(): String = intent.getStringExtra(User.PHONE)!!

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
                userRepository.getByPhoneNumber(getMyPhoneNumber(), this, true)
            } else {
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    verifyPresenterCallback.showWrongCodeError()
                }
            }
        }
    }
    /*override fun returnWrongCodeError() {
        verifyPresenterCallback.showWrongCodeError()
    }

    override fun returnVerifySuccessful(credential: PhoneAuthCredential) {
        userRepository.getByPhoneNumber(getMyPhoneNumber(), this, true)
    }*/

    override fun returnUsers(users: List<User>) {
        if (users.isEmpty() || users.first().name.isEmpty()) {
            verifyPresenterCallback.goToRegistration(getMyPhoneNumber())
        } else {
            verifyPresenterCallback.goToProfile()
        }
    }

    companion object {
        const val TAG = "DBInf"
    }

}