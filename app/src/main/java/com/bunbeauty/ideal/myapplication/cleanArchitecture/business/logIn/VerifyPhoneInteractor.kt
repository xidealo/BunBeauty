package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn

import android.content.Intent
import android.util.Log
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.iLogIn.IVerifyPhoneInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.VerifyPhonePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.user.IUserCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.logIn.VerifyPhoneActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.BaseRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.UserRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.interfaceRepositories.IUserRepository
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class VerifyPhoneInteractor(private val userRepository: IUserRepository,
                            private val intent: Intent) : BaseRepository(),
        IVerifyPhoneInteractor, IUserCallback {

    lateinit var verifyPresenterCallback: VerifyPhonePresenterCallback
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var phoneVerificationId: String

    override fun getMyPhoneNumber(): String = intent.getStringExtra(USER_PHONE)!!

    fun getMyName() {
        userRepository.getByPhoneNumber(getMyPhoneNumber(), this, true)
    }

    override fun returnUser(user: User) {
        verifyPresenterCallback.callbackGetUserName(user.name)
    }

    override fun sendVerificationCode(phoneNumber: String, activity: VerifyPhoneActivity) {
        Log.d(TAG, "send")
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber, // Phone number to verify
                60, // Timeout duration
                TimeUnit.SECONDS, // Unit of timeout
                activity, // Activity (for callback binding)
                verificationCallbacks)
    }

    override fun resendVerificationCode(phoneNumber: String, token: PhoneAuthProvider.ForceResendingToken,
                                       verifyPhoneActivity: VerifyPhoneActivity) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber, // Phone number to verify
                60, // Timeout duration
                TimeUnit.SECONDS, // Unit of timeout
                verifyPhoneActivity, // Activity (for callback binding)
                verificationCallbacks, // OnVerificationStateChangedCallbacks
                token)  // ForceResendingToken from callbacks
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

    private val verificationCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
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

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
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

    fun callbackGetUserName(name: String) {
        if (name == "") {
            verifyPresenterCallback.goToRegistration(getMyPhoneNumber())
        } else {
            verifyPresenterCallback.goToProfile()
        }
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

    companion object{
        private val TAG = "DBInf"
        private const val USER_PHONE = "user phone"
    }
}