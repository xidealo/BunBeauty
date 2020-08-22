package com.bunbeauty.ideal.myapplication.clean_architecture.callback

import com.google.firebase.auth.PhoneAuthCredential

interface VerifyPhoneNumberCallback {
    fun returnVerificationFailed()
    fun returnTooManyRequestsError()
    fun returnTooShortCodeError()
    fun returnCredential(credential: PhoneAuthCredential)
    fun returnServiceConnectionProblem()
}