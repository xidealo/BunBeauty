package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.iLogIn

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.VerifyPhonePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.logIn.VerifyPhoneActivity
import com.google.firebase.auth.PhoneAuthProvider

interface IVerifyPhoneInteractor {
    fun sendVerificationCode(phoneNumber: String, activity: VerifyPhoneActivity)
    fun resendVerificationCode(phoneNumber: String, token: PhoneAuthProvider.ForceResendingToken,
                               verifyPhoneActivity: VerifyPhoneActivity)

    fun getMyPhoneNumber(): String
    fun verify(code: String, verifyPresenterCallback: VerifyPhonePresenterCallback)

}