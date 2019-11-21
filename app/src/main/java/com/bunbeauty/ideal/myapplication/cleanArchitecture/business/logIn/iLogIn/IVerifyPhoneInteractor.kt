package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.iLogIn

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.IVerifyCallback

interface IVerifyPhoneInteractor {
    fun getMyPhoneNumber(): String
    fun getMyName(verifyCallback: IVerifyCallback)
}