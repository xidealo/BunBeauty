package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.iLogIn

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.logIn.RegistrationPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface IRegistrationUserInteractor {
    fun registerUser(user: User, registrationPresenterCallback: RegistrationPresenterCallback)
    fun getMyPhoneNumber(): String
}