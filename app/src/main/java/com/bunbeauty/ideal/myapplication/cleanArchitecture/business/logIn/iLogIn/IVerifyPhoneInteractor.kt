package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.iLogIn

import android.app.Activity
import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.ui.activities.logIn.VerifyPhoneActivity
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

interface IVerifyPhoneInteractor {
    fun verifyCode(phoneNumber: String, code: String, verifyPhoneActivity: VerifyPhoneActivity)
    fun sendVerificationCode(phoneNumber: String, activity: Activity)
    fun resendVerificationCode(phoneNumber: String, activity: Activity, token: PhoneAuthProvider.ForceResendingToken)
    fun signInWithPhoneAuthCredential(phoneNumber: String, credential: PhoneAuthCredential, verifyPhoneActivity: VerifyPhoneActivity)
    fun getResendToken(): PhoneAuthProvider.ForceResendingToken?
    fun getMyPhoneNumber(intent: Intent) :String
}