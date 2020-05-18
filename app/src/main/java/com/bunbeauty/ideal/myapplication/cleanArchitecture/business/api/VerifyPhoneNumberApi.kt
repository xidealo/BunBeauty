package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.api

import android.app.Activity
import androidx.annotation.MainThread
import androidx.core.content.ContextCompat
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.VerifyPhoneNumberCallback
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class VerifyPhoneNumberApi {

    private lateinit var verifyPhoneNumberCallback: VerifyPhoneNumberCallback
    private lateinit var phoneVerificationId: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    fun sendVerificationCode(
        phoneNumber: String,
        verifyPhoneNumberCallback: VerifyPhoneNumberCallback
    ) {
        this.verifyPhoneNumberCallback = verifyPhoneNumberCallback

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,
            60,
            TimeUnit.SECONDS,
            TaskExecutors.MAIN_THREAD,
            verificationCallbacks
        )
    }

    fun resendVerificationCode(phoneNumber: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,
            60,
            TimeUnit.SECONDS,
            TaskExecutors.MAIN_THREAD,
            verificationCallbacks,
            resendToken
        )
    }

    fun checkCode(code: String, verifyPhoneNumberCallback: VerifyPhoneNumberCallback) {
        if (code.trim().length < 6) {
            verifyPhoneNumberCallback.returnTooShortCodeError()
            return
        }

        val credential = PhoneAuthProvider.getCredential(phoneVerificationId, code)

        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                verifyPhoneNumberCallback.returnVerifySuccessful()
            } else {
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    verifyPhoneNumberCallback.returnWrongCodeError()
                }
            }
        }
    }

    private val verificationCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                verifyPhoneNumberCallback.returnVerifySuccessful()
            }

            override fun onVerificationFailed(e: FirebaseException) {
                if (e is FirebaseAuthInvalidCredentialsException) {
                    verifyPhoneNumberCallback.returnVerificationFailed()
                } else if (e is FirebaseTooManyRequestsException) {
                    verifyPhoneNumberCallback.returnTooManyRequestsError()
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                phoneVerificationId = verificationId
                resendToken = token
            }
        }


}