package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.VerifyPhoneInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.VerifyCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.VerifyPhoneView
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.logIn.VerifyPhoneActivity
import com.bunbeauty.ideal.myapplication.helpApi.LoadingGuestServiceData
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.*
import java.util.concurrent.TimeUnit

@InjectViewState
class VerifyPhonePresenter(private val verifyPhoneInteractor: VerifyPhoneInteractor) :
        MvpPresenter<VerifyPhoneView>(), VerifyCallback {

    private val TAG = "DBInf"
    private lateinit var phoneVerificationId: String

    var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    fun sendCode(verifyPhoneActivity: VerifyPhoneActivity) {
        sendVerificationCode(verifyPhoneInteractor.getMyPhoneNumber(),
                verifyPhoneActivity)
        viewState.showSendCode()
    }

    fun resendCode(verifyPhoneActivity: VerifyPhoneActivity) {
        resendVerificationCode(verifyPhoneInteractor.getMyPhoneNumber(),
                resendToken!!,
                verifyPhoneActivity)
        viewState.showSendCode()
    }

    fun verify(code: String) {
        if (code.trim().length >= 6) {
            verifyCode(code)
            viewState.hideViewsOnScreen()
        } else {
            viewState.showWrongCode()
        }
    }

    private fun verifyCode(code: String) {
        //получаем ответ гугл
        val credential = PhoneAuthProvider.getCredential(phoneVerificationId, code)
        //заходим с айфоном и токеном
        signInWithPhoneAuthCredential(credential)
    }

    override fun callbackGetUserName(name: String) {
        if (name.isEmpty()) {
            viewState.goToRegistration()
        } else {
            viewState.goToProfile()
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        val fbAuth: FirebaseAuth = FirebaseAuth.getInstance()

        fbAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            //если введен верный код
            if (task.isSuccessful) {
                verifyPhoneInteractor.getMyName(this)
            } else {
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    viewState.callbackWrongCode()
                }
            }
        }
    }

    private fun sendVerificationCode(phoneNumber: String, activity: VerifyPhoneActivity) {
        Log.d(TAG, "send")
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber, // Phone number to verify
                60, // Timeout duration
                TimeUnit.SECONDS, // Unit of timeout
                activity, // Activity (for callback binding)
                verificationCallbacks)
    }

    private fun resendVerificationCode(phoneNumber: String, token: PhoneAuthProvider.ForceResendingToken,
                                       verifyPhoneActivity: VerifyPhoneActivity) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber, // Phone number to verify
                60, // Timeout duration
                TimeUnit.SECONDS, // Unit of timeout
                verifyPhoneActivity, // Activity (for callback binding)
                verificationCallbacks, // OnVerificationStateChangedCallbacks
                token)  // ForceResendingToken from callbacks
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
}