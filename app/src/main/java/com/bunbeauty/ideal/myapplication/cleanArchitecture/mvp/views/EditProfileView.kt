package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views

import com.arellomobile.mvp.MvpView
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface EditProfileView: MvpView {

    fun showEditProfile(user: User)
    fun showAvatar(photoLink: String)

    fun showCode()
    fun hideCode()

    fun showVerifyCode()
    fun hideVerifyCode()

    fun showResentCode()
    fun hideResentCode()

    fun setNameEditProfileInputError(error: String)
    fun setSurnameEditProfileInputError(error: String)
    fun setPhoneEditProfileInputError(error: String)

    fun disableEditProfileEditButton()
    fun enableEditProfileEditButton()
    fun showNoSelectedCity()
    fun showPhoneError(error:String)




}