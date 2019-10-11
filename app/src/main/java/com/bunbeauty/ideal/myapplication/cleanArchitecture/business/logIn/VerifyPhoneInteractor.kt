package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn

import android.app.Activity
import android.util.Log
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.iLogIn.IVerifyPhoneInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.VerifyCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.ui.activities.logIn.VerifyPhoneActivity
import com.bunbeauty.ideal.myapplication.logIn.MyAuthorization
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider.*
import java.util.concurrent.TimeUnit

class VerifyPhoneInteractor : IVerifyPhoneInteractor {
    private val TAG = "DBInf"
    private lateinit var resendToken: ForceResendingToken
    private lateinit var phoneVerificationId: String

    override fun verifyCode(phoneNumber: String, code: String, verifyPhoneActivity: VerifyPhoneActivity) {
        //получаем ответ гугл
        val credential = getCredential(phoneVerificationId, code)
        //заходим с айфоном и токеном
        signInWithPhoneAuthCredential(phoneNumber, credential, verifyPhoneActivity)
    }

    private val verificationCallbacks = object : OnVerificationStateChangedCallbacks() {

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

        override fun onCodeSent(verificationId: String, p1: ForceResendingToken) {
            //происходит, когда отослали код
            phoneVerificationId = verificationId
            resendToken = p1
        }
    }

    override fun sendVerificationCode(phoneNumber: String, verifyPhoneActivity: VerifyPhoneActivity) {
        getInstance().verifyPhoneNumber(
                phoneNumber, // Phone number to verify
                60, // Timeout duration
                TimeUnit.SECONDS, // Unit of timeout
                verifyPhoneActivity, // Activity (for callback binding)
                verificationCallbacks)
    }

    override fun resendVerificationCode(phoneNumber: String, token: ForceResendingToken, verifyPhoneActivity: VerifyPhoneActivity) {
        getInstance().verifyPhoneNumber(
                phoneNumber, // Phone number to verify
                60, // Timeout duration
                TimeUnit.SECONDS, // Unit of timeout
                verifyPhoneActivity, // Activity (for callback binding)
                verificationCallbacks, // OnVerificationStateChangedCallbacks
                token)  // ForceResendingToken from callbacks
    }

    override fun signInWithPhoneAuthCredential(phoneNumber: String, credential: PhoneAuthCredential, verifyPhoneActivity: VerifyPhoneActivity) {
        val fbAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val verifyCallback: VerifyCallback = verifyPhoneActivity

        fbAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            //если введен верный код
            if (task.isSuccessful) {
                val myAuth = MyAuthorization(Activity(), phoneNumber)
                myAuth.authorizeUser()
            } else {
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    verifyCallback.callbackWrongCode()
                }
            }
        }
    }

    override fun getResendToken(): ForceResendingToken? {
        return resendToken
    }

    override fun getMyPhoneNumber(): String {
        //return intent.getStringExtra(PHONE_NUMBER)
        return "+79100080142" //something from db
    }
}