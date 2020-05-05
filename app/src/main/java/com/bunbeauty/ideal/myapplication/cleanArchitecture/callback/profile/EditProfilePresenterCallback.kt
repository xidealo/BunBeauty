package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.profile

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface EditProfilePresenterCallback {
    fun showEditProfile(user: User)
    fun goToProfile(user: User)
    fun nameEditProfileInputError()
    fun nameEditProfileInputErrorEmpty()
    fun nameEditProfileInputErrorLong()
    fun surnameEditProfileInputError()
    fun surnameEditProfileInputErrorEmpty()
    fun surnameEditProfileEditErrorLong()
    fun cityEditProfileInputError()
    fun phoneEditProfileInputError()
}