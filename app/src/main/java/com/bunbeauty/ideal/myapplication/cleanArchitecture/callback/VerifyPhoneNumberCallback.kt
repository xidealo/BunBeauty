package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback

import com.google.firebase.auth.PhoneAuthCredential

interface VerifyPhoneNumberCallback {
    //fun returnVerifySuccessful(credential: PhoneAuthCredential)

    fun returnVerificationFailed()
    fun returnTooManyRequestsError()
    fun returnTooShortCodeError()
    //fun returnWrongCodeError()
    fun returnCredential(credential: PhoneAuthCredential)
}