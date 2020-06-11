package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.logIn

import com.arellomobile.mvp.MvpView
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface RegistrationView : MvpView {
    fun setNameInputError(error: String)
    fun setSurnameInputError(error: String)
    fun showNoSelectedCity()
    fun goToProfile(user: User)
    fun fillPhoneInput(phone: String)
    fun showSuccessfulRegistration()
    fun disableRegistrationButton()
    fun enableRegistrationButton()
}