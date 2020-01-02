package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.logIn

interface IRegistrationPresenter {
    fun showSuccessfulRegistration()
    fun registrationNameInputError()
    fun registrationNameInputErrorEmpty()
    fun registrationNameInputErrorLong()
    fun registrationSurnameInputError()
    fun registrationSurnameInputErrorEmpty()
    fun registrationSurnameInputErrorLong()
    fun registrationCityInputError()
}