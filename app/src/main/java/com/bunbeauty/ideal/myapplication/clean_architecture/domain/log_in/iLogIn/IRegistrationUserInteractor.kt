package com.bunbeauty.ideal.myapplication.clean_architecture.domain.log_in.iLogIn

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.logIn.RegistrationPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User

interface IRegistrationUserInteractor {
    fun registerUser(user: User, registrationPresenterCallback: RegistrationPresenterCallback)
    fun getMyPhoneNumber(): String
}