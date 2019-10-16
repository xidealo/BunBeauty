package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.VerifyPhoneInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.VerifyCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.VerifyPhoneView
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.logIn.VerifyPhoneActivity
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

@InjectViewState
class VerifyPhonePresenter(private val verifyPhoneInteractor: VerifyPhoneInteractor): MvpPresenter<VerifyPhoneView>() {

    private val TAG = "DBInf"
    private lateinit var phoneVerificationId: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    fun sendCode(verifyPhoneActivity: VerifyPhoneActivity){
        sendVerificationCode(verifyPhoneInteractor.getMyPhoneNumber(),  verifyPhoneActivity)
        viewState.showResendCode()
    }

    fun verify(code:String, verifyPhoneActivity: VerifyPhoneActivity){
        if (code.trim().length >= 6) {
            verifyCode(code, verifyPhoneActivity)
            viewState.hideViewsOnScreen()
        }else{
            viewState.showWrongCode()
        }
    }

    fun resendCode(verifyPhoneActivity: VerifyPhoneActivity){
        if (getResendToken() != null) {
            resendVerificationCode(verifyPhoneInteractor.getMyPhoneNumber(),
                    getResendToken()!!, verifyPhoneActivity)
            viewState.showResendCode()
        }
    }

    private fun getResendToken(): PhoneAuthProvider.ForceResendingToken? {
        return resendToken
    }

    private fun verifyCode(code: String, verifyPhoneActivity: VerifyPhoneActivity) {
        //получаем ответ гугл
        val credential = PhoneAuthProvider.getCredential(phoneVerificationId, code)
        //заходим с айфоном и токеном
        signInWithPhoneAuthCredential(credential, verifyPhoneActivity)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential, verifyPhoneActivity: VerifyPhoneActivity) {
        val fbAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val verifyCallback: VerifyCallback = verifyPhoneActivity

        fbAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            //если введен верный код
            if (task.isSuccessful) {
                    verifyCallback.goToRegistration()
            } else {
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    verifyCallback.callbackWrongCode()
                }
            }
        }
    }

    private fun sendVerificationCode(phoneNumber: String, verifyPhoneActivity: VerifyPhoneActivity) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber, // Phone number to verify
                60, // Timeout duration
                TimeUnit.SECONDS, // Unit of timeout
                verifyPhoneActivity, // Activity (for callback binding)
                verificationCallbacks)
    }

    private fun resendVerificationCode(phoneNumber: String, token: PhoneAuthProvider.ForceResendingToken, verifyPhoneActivity: VerifyPhoneActivity) {
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

        override fun onCodeSent(verificationId: String, p1: PhoneAuthProvider.ForceResendingToken) {
            //происходит, когда отослали код
            phoneVerificationId = verificationId
            resendToken = p1
        }
    }
}