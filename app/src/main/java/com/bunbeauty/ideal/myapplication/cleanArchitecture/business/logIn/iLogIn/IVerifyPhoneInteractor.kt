package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.iLogIn

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.VerifyCallback

interface IVerifyPhoneInteractor {
    fun getMyPhoneNumber(verifyCallback: VerifyCallback)
}