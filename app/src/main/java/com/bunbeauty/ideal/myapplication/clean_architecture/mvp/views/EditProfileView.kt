package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views

import com.arellomobile.mvp.MvpView
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User

interface EditProfileView: MvpView {

    fun showEditProfile(user: User)
    fun showAvatar(photoLink: String)
    fun goToProfile(user: User)

    fun setNameEditProfileInputError(error: String)
    fun setSurnameEditProfileInputError(error: String)
    fun setPhoneEditProfileInputError(error: String)

    fun disableEditProfileEditButton()
    fun enableEditProfileEditButton()
    fun showPhoneError(error:String)
    fun showLoading()
    fun showCodeInputAndButtons()
    fun hideCodeInputAndButtons()
    fun showMessage(message: String)
}