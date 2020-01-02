package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views

import com.arellomobile.mvp.MvpView

interface RegistrationView: MvpView {
    fun setNameInputError(error:String)
    fun setSurnameInputError(error:String)
    fun showNoSelectedCity()
    fun goToProfile()
    fun fillPhoneInput(phone: String)
    fun showSuccessfulRegistration()
    fun disableRegistrationButtnon()
    fun enableRegistrationButtnon()
}