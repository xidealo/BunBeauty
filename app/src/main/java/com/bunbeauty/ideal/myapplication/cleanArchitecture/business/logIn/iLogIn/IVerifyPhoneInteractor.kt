package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.iLogIn

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.VerifyPhonePresenterCallback

interface IVerifyPhoneInteractor {
    fun sendVerificationCode(
            phoneNumber: String,
            verifyPhonePresenterCallback: VerifyPhonePresenterCallback)

    fun resendVerificationCode(
            phoneNumber: String,
            verifyPhonePresenterCallback: VerifyPhonePresenterCallback)

    fun getMyPhoneNumber(): String
    fun verify(code: String, verifyPresenterCallback: VerifyPhonePresenterCallback)

}