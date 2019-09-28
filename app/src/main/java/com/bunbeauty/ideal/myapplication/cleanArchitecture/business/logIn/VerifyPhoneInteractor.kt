package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.VerifyCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.presentation.logIn.VerifyPhoneActivity
import com.bunbeauty.ideal.myapplication.logIn.MyAuthorization
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider.*
import java.util.concurrent.TimeUnit

class VerifyPhoneInteractor(private val context: Context) {
    private val TAG = "DBInf"
    private var resendToken: ForceResendingToken? = null
    private var phoneVerificationId: String = ""
    private val PHONE_NUMBER = "phone number"



    fun verifyCode(phoneNumber: String, code: String, verifyPhoneActivity: VerifyPhoneActivity) {
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

        override fun onCodeSent(verificationId: String, token: ForceResendingToken?) {
            //происходит, когда отослали код
            phoneVerificationId = verificationId
            resendToken = token
        }
    }

    fun sendVerificationCode(phoneNumber: String, activity: Activity) {

        getInstance().verifyPhoneNumber(
                phoneNumber, // Phone number to verify
                60, // Timeout duration
                TimeUnit.SECONDS, // Unit of timeout
                activity, // Activity (for callback binding)
                verificationCallbacks)
    }

    fun resendVerificationCode(phoneNumber: String, activity: Activity, token: ForceResendingToken) {
        getInstance().verifyPhoneNumber(
                phoneNumber, // Phone number to verify
                60, // Timeout duration
                TimeUnit.SECONDS, // Unit of timeout
                activity, // Activity (for callback binding)
                verificationCallbacks, // OnVerificationStateChangedCallbacks
                token)  // ForceResendingToken from callbacks
    }

    private fun signInWithPhoneAuthCredential(phoneNumber: String, credential: PhoneAuthCredential, verifyPhoneActivity: VerifyPhoneActivity) {
        val fbAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val verifyCallback: VerifyCallback = verifyPhoneActivity

        fbAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            //если введен верный код
            if (task.isSuccessful) {
                val myAuth = MyAuthorization(context, phoneNumber)
                myAuth.authorizeUser()
            } else {
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    verifyCallback.callbackWrongCode()
                }
            }
        }
    }

    fun getResendToken(): ForceResendingToken? {
        return resendToken
    }

    fun getMyPhoneNumber(intent: Intent): String {
        return intent.getStringExtra(PHONE_NUMBER)
    }
}