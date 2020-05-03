package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn

import android.content.Intent
import android.util.Log
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.iLogIn.IVerifyPhoneInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.VerifyPhonePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.user.UsersCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.BaseRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IUserRepository
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class VerifyPhoneInteractor(
    private val userRepository: IUserRepository,
    private val intent: Intent
) : BaseRepository(),
    IVerifyPhoneInteractor, UsersCallback {

    lateinit var verifyPresenterCallback: VerifyPhonePresenterCallback
    var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    private var phoneVerificationId: String = "1"

    override fun getMyPhoneNumber(): String = intent.getStringExtra(User.PHONE)!!

    private fun getMyName() {
        userRepository.getByPhoneNumber(getMyPhoneNumber(), this, true)
    }

    override fun returnUsers(users: List<User>) {
        if (users.first().name.isEmpty()) {
            verifyPresenterCallback.goToRegistration(getMyPhoneNumber())
        } else {
            verifyPresenterCallback.goToProfile()
        }
    }
    override fun sendVerificationCode(
        phoneNumber: String,
        verifyPhonePresenterCallback: VerifyPhonePresenterCallback
    ) {
        Log.d(TAG, "send")
        verifyPhonePresenterCallback.sendVerificationCode(phoneNumber, verificationCallbacks)
        verifyPhonePresenterCallback.showSendCode()
    }

    override fun resendVerificationCode(
        phoneNumber: String,
        verifyPhonePresenterCallback: VerifyPhonePresenterCallback
    ) {
        if (resendToken != null) {
            verifyPhonePresenterCallback.resendVerificationCode(
                phoneNumber,
                verificationCallbacks,
                resendToken!!
            )
            verifyPhonePresenterCallback.showSendCode()
        }
    }

    override fun verify(code: String, verifyPresenterCallback: VerifyPhonePresenterCallback) {
        this.verifyPresenterCallback = verifyPresenterCallback

        if (code.trim().length >= 6) {
            verifyCode(code)
            verifyPresenterCallback.hideViewsOnScreen()
        } else {
            verifyPresenterCallback.showWrongCode()
        }
    }

    private val verificationCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                //вызывается, если номер подтвержден
            }

            override fun onVerificationFailed(e: FirebaseException) {
                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Log.d(TAG, "Invalid credential: " + e.getLocalizedMessage())
                } else if (e is FirebaseTooManyRequestsException) {
                    // SMS quota exceeded
                    Log.d(TAG, "SMS Quota exceeded.")
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                //происходит, когда отослали код
                phoneVerificationId = verificationId
                resendToken = token
            }
        }

    private fun verifyCode(code: String) {
        //получаем ответ гугл
        val credential = PhoneAuthProvider.getCredential(phoneVerificationId, code)
        //заходим с айфоном и токеном
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        val fbAuth: FirebaseAuth = FirebaseAuth.getInstance()

        fbAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            //если введен верный код
            if (task.isSuccessful) {
                getMyName()
            } else {
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    verifyPresenterCallback.callbackWrongCode()
                }
            }
        }
    }

    companion object {
        const val TAG = "DBInf"
    }

}