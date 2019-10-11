package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.iLogIn

import com.bunbeauty.ideal.myapplication.cleanArchitecture.ui.activities.logIn.VerifyPhoneActivity
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

interface IVerifyPhoneInteractor {
    fun verifyCode(phoneNumber: String, code: String, verifyPhoneActivity: VerifyPhoneActivity)
    fun sendVerificationCode(phoneNumber: String, verifyPhoneActivity: VerifyPhoneActivity)
    fun resendVerificationCode(phoneNumber: String, token: PhoneAuthProvider.ForceResendingToken, verifyPhoneActivity: VerifyPhoneActivity)
    fun signInWithPhoneAuthCredential(phoneNumber: String, credential: PhoneAuthCredential, verifyPhoneActivity: VerifyPhoneActivity)
    fun getResendToken(): PhoneAuthProvider.ForceResendingToken?
    fun getMyPhoneNumber() :String
}