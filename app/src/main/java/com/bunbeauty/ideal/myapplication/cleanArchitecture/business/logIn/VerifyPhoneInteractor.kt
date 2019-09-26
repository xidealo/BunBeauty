package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider.*
import java.util.concurrent.TimeUnit

class VerifyPhoneInteractor {
    private val TAG = "DBInf"
    private var resendToken: ForceResendingToken? = null
    private var phoneVerificationId: String = ""
    private val PHONE_NUMBER = "phone number"

    fun verifyCode(code: String) {
        //получаем ответ гугл
        val credential = getCredential(phoneVerificationId, code)
        //заходим с айфоном и токеном
        signInWithPhoneAuthCredential(credential)
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

    fun sendVerificationCode(myPhoneNumber: String,
                             activity: Activity) {
        getInstance().verifyPhoneNumber(
                myPhoneNumber, // Phone number to verify
                60, // Timeout duration
                TimeUnit.SECONDS, // Unit of timeout
                activity, // Activity (for callback binding)
                verificationCallbacks)
    }

    fun resendVerificationCode(myPhoneNumber: String, activity: Activity, token: ForceResendingToken) {
        getInstance().verifyPhoneNumber(
                myPhoneNumber, // Phone number to verify
                60, // Timeout duration
                TimeUnit.SECONDS, // Unit of timeout
                activity, // Activity (for callback binding)
                verificationCallbacks, // OnVerificationStateChangedCallbacks
                token)  // ForceResendingToken from callbacks
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        val fbAuth: FirebaseAuth = FirebaseAuth.getInstance()

        fbAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            //если введен верный код
            if (task.isSuccessful) {
                /* val myAuth = MyAuthorization()
                        myAuth.authorizeUser()*/
            } else {
                if (task.exception is FirebaseAuthInvalidCredentialsException) {

                    /* showViewsOfScreen()
                     assertWrongCode()
                     codeInput.setError("Неправильный код")
                     codeInput.requestFocus()*/
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