package com.bunbeauty.ideal.myapplication.clean_architecture.domain.log_in.iLogIn

import android.content.Intent
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.VerifyPhonePresenterCallback

interface IVerifyPhoneInteractor {

    fun getPhoneNumber(intent: Intent): String

    fun sendVerificationCode(
        phoneNumber: String,
        verifyPresenterCallback: VerifyPhonePresenterCallback
    )
    fun resendVerificationCode(phoneNumber: String)
    fun checkCode(code: String)

}