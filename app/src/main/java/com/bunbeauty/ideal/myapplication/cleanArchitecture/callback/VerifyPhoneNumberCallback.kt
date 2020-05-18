package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback

interface VerifyPhoneNumberCallback {
    fun returnVerifySuccessful()

    fun returnVerificationFailed()
    fun returnTooManyRequestsError()
    fun returnTooShortCodeError()
    fun returnWrongCodeError()
}