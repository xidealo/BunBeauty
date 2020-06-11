package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.logIn

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface RegistrationPresenterCallback {
    fun showSuccessfulRegistration(user: User)
    fun registrationNameInputError()
    fun registrationNameInputErrorEmpty()
    fun registrationNameInputErrorLong()
    fun registrationSurnameInputError()
    fun registrationSurnameInputErrorEmpty()
    fun registrationSurnameInputErrorLong()
    fun registrationCityInputError()
}