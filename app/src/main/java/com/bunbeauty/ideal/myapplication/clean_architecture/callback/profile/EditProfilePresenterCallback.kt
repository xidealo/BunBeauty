package com.bunbeauty.ideal.myapplication.clean_architecture.callback.profile

import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User

interface EditProfilePresenterCallback {
    fun showEditProfile(user: User)
    fun goToProfile(user: User)
    fun nameEditProfileInputError()
    fun nameEditProfileInputErrorEmpty()
    fun nameEditProfileInputErrorLong()
    fun surnameEditProfileInputError()
    fun surnameEditProfileInputErrorEmpty()
    fun surnameEditProfileEditErrorLong()
    fun phoneEditProfileInputError()
    fun returnCodeSent()
    fun showTooManyRequestsError()
    fun showTooShortCodeError()
    fun showVerificationFailed()
    fun showWrongCodeError()
    fun showServiceConnectionProblem()
    fun showLoading()
    fun showPhoneAlreadyUsedError()
    fun savePhotos(photos: List<Photo>, user: User)
    fun deletePreviousPhoto(photos: ArrayList<Photo>)
}