package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.iLogIn

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.VerifyPhonePresenterCallback

interface IVerifyPhoneInteractor {

    fun getMyPhoneNumber(): String

    fun sendVerificationCode(
        phoneNumber: String,
        verifyPresenterCallback: VerifyPhonePresenterCallback
    )
    fun resendVerificationCode(phoneNumber: String)
    fun checkCode(code: String)

}