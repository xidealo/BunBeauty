package com.bunbeauty.ideal.myapplication.clean_architecture.business.log_in.iLogIn

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.VerifyPhonePresenterCallback

interface IVerifyPhoneInteractor {

    fun getPhoneNumber(): String

    fun sendVerificationCode(
        phoneNumber: String,
        verifyPresenterCallback: VerifyPhonePresenterCallback
    )
    fun resendVerificationCode(phoneNumber: String)
    fun checkCode(code: String)

}